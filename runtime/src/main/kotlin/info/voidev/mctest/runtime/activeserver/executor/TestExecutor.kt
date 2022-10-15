package info.voidev.mctest.runtime.activeserver.executor

import info.voidev.mctest.api.MCTest
import info.voidev.mctest.api.TestPlayer
import info.voidev.mctest.runtime.activeserver.lib.parambind.TestPlayerParamBinder
import info.voidev.mctest.api.TickFunctionScope
import info.voidev.mctest.runtime.activeserver.lib.parambind.CollectiveParamBinder
import info.voidev.mctest.runtime.activeserver.lib.parambind.TestScopeParamBinder
import info.voidev.mctest.runtime.activeserver.lib.testplayer.TestPlayerClientImpl
import info.voidev.mctest.runtime.activeserver.lib.tickfunction.launchTickFunction
import info.voidev.mctest.runtime.activeserver.testeePluginInstance
import info.voidev.mctest.runtimesdk.InvalidTestException
import info.voidev.mctest.runtimesdk.util.IsMcTestMethod
import org.bukkit.Bukkit
import org.objectweb.asm.Type
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("unused") // Used via reflection
class TestExecutor {

    private val instanceCache = TestClassInstanceCache()

    private val paramBinder = CollectiveParamBinder(listOf(TestScopeParamBinder(), TestPlayerParamBinder()))

    fun executeTestMethod(testClassName: String, methodName: String, methodDescriptor: String) {
        require(!Bukkit.isPrimaryThread())

        val testClass = Class.forName(testClassName)

        val testMethod = testClass.declaredMethods.first { m ->
            m.name == methodName && Type.getMethodDescriptor(m) == methodDescriptor
        }
        require(IsMcTestMethod.test(testMethod))

        val instance = instanceCache[testClass]

        val testSpec = testMethod.getAnnotation(MCTest::class.java)!!

        // We support--in fact, encourage--marking test methods as suspend to be able to yield ticks.
        // However, this is not a hard requirement, although a coroutine will still be created even if the test is not suspend.
        val isSuspend = isSuspendMethod(testMethod)

        val future = CompletableFuture<Unit>()
        Bukkit.getScheduler().runTask(testeePluginInstance, Runnable {
            launchTickFunction {
                executeSuspendingTest(testMethod, instance, isSuspend)
            }.handle { _, ex ->
                if (ex != null) future.completeExceptionally(ex)
                else future.complete(Unit)
            }
        })

        // Block until test is finished
        try {
            future.get()
        } catch (ex: ExecutionException) {
            throw ex.cause ?: ex
        }
    }

    private suspend fun TickFunctionScope.executeSuspendingTest(
        testMethod: Method,
        instance: Any?,
        isSuspend: Boolean,
    ) {
        val scopeBuilder = TestScopeBuilder(this)

        // Bind non-continuation method parameters
        val paramsExceptContinuation =
            if (isSuspend) testMethod.parameters.asList().subList(0, testMethod.parameterCount - 1)
            else testMethod.parameters.asList()
        val argsExceptContinuation = bindParams(paramsExceptContinuation, scopeBuilder)

        // Directly before invoking the test method, synchronize all test players
        scopeBuilder.syncPackets()

        try {
            suspendCoroutine<Any?> { cont ->
                val args = if (isSuspend) (argsExceptContinuation + cont) else argsExceptContinuation
                val retVal = invokeTestMethod(testMethod, instance, args.toTypedArray())
                if (retVal !== kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED) {
                    // This means that there wasn't a single call to yieldTick() in the method,
                    //  in which case every tick() call in TickFunctionDriver causes an NPE:
                    //  java.lang.NullPointerException: null
                    //	  at kotlin.coroutines.jvm.internal.ContinuationImpl.releaseIntercepted(ContinuationImpl.kt:118)
                    //	  at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:39)
                    //	  at info.voidev.mctest.runtime.activeserver.lib.tickfunction.TickFunctionDriver.tick(TickFunctionDriver.kt:43)
                    // Apparently, calling cont.resume() here fixes that. TODO: Figure out why
                    cont.resume(Unit)
                }
            }
        } finally {
            // Disconnect any test players
            for (arg in argsExceptContinuation) {
                if (arg is TestPlayer) {
                    (arg.client as TestPlayerClientImpl).disconnect()
                }
            }
        }
    }

    private fun isSuspendMethod(m: Method): Boolean {
        return m.parameterCount > 0 && m.parameterTypes.last() == Continuation::class.java
    }

    private suspend fun bindParams(params: List<Parameter>, scopeBuilder: TestScopeBuilder): List<Any?> {
        return params.map { param ->
            if (paramBinder.canBind(param, scopeBuilder)) {
                paramBinder.bind(param, scopeBuilder)
            } else {
                throw InvalidTestException("Don't know how to bind parameter $param")
            }
        }
    }

    private fun invokeTestMethod(m: Method, instance: Any?, args: Array<Any?>): Any? {
        try {
            return m.invoke(instance, *args)
        } catch (ex: InvocationTargetException) {
            // Unwrap thrown exceptions (the engine/the user doesn't need to know about this implementation detail)
            throw ex.cause ?: ex
        }
    }

}
