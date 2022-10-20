package info.voidev.mctest.engine.execution

import info.voidev.mctest.engine.MctestEngineDescriptor
import info.voidev.mctest.engine.config.JUnitMctestConfig
import info.voidev.mctest.engine.discovery.ClassTestDescriptor
import info.voidev.mctest.engine.discovery.MethodTestDescriptor
import info.voidev.mctest.engine.server.TestableMinecraftServer
import info.voidev.mctest.engine.server.TestableServerSession
import info.voidev.mctest.engine.util.unwrapRmiExceptions
import org.junit.platform.engine.ConfigurationParameters
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.ReportEntry
import org.objectweb.asm.Type
import java.lang.reflect.InvocationTargetException
import kotlin.system.measureTimeMillis

class McTestExecutor(
    private val root: MctestEngineDescriptor,
    private val listener: EngineExecutionListener,
    params: ConfigurationParameters,
) {

    private val config = JUnitMctestConfig(params)

    fun execute() {
        listener.executionStarted(root)

        var server: TestableMinecraftServer? = null
        try {
            server = TestableMinecraftServer(config)
            server.start()

            val serverSession = server.requireActiveSession()
            val tookMillis = measureTimeMillis {
                serverSession.engine.waitForServerToStart()
            }
            listener.reportingEntryPublished(root, ReportEntry.from("mctest.serverStartTimeMs", "%.2fs".format(tookMillis.toDouble() / 1000)))

            // Run the tests
            for (child in root.children) {
                executeClass(child as ClassTestDescriptor, serverSession)
            }

            listener.executionFinished(root, TestExecutionResult.successful())
        } catch (ex: Throwable) {
            listener.executionFinished(root, TestExecutionResult.failed(ex))
        } finally {
            server?.stop()
        }
    }

    private fun executeClass(testClass: ClassTestDescriptor, serverSession: TestableServerSession) {
        listener.executionStarted(testClass)

        try {
            // Run the tests inside the class
            for (child in testClass.children) {
                executeTestMethod(child as MethodTestDescriptor, serverSession)
            }

            listener.executionFinished(testClass, TestExecutionResult.successful())
        } catch (ex: Exception) {
            listener.executionFinished(testClass, TestExecutionResult.failed(ex))
        }
    }

    private fun executeTestMethod(testMethod: MethodTestDescriptor, serverSession: TestableServerSession) {
        listener.executionStarted(testMethod)

        try {
            // Test method should be executed on runtime side, not engine
            //  (=> use RMI service to request test execution)
            unwrapRmiExceptions {
                serverSession.runtime.runTestMethod(
                    testMethod.method.declaringClass.name,
                    testMethod.method.name,
                    Type.getMethodDescriptor(testMethod.method)
                )
            }

            listener.executionFinished(testMethod, TestExecutionResult.successful())
        } catch (ex: Throwable) {
            listener.executionFinished(testMethod, TestExecutionResult.failed((ex as? InvocationTargetException)?.targetException ?: ex))
        }
    }

}
