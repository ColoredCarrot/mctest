package info.voidev.mctest.runtime.proto

import info.voidev.mctest.runtime.EngineHolder
import info.voidev.mctest.runtime.classloading.MctestRuntimeClassLoader
import info.voidev.mctest.runtimesdk.proto.Monostate
import info.voidev.mctest.runtimesdk.proto.RuntimeService
import org.bukkit.Bukkit
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class RuntimeServiceImpl : RuntimeService {

    private val executeTestMethodHandle by lazy {
        val c = Class.forName("info.voidev.mctest.runtime.activeserver.executor.TestExecutor", true, MctestRuntimeClassLoader.instance)
        val executor = c.getConstructor().newInstance()
        MethodHandles.publicLookup()
            .findVirtual(
                c,
                "executeTestMethod",
                MethodType.methodType(Void.TYPE, String::class.java, String::class.java, String::class.java)
            )
            .bindTo(executor)
    }

    private val exceptionNormalizer by lazy {
        ExceptionNormalizer(EngineHolder.INSTANCE)
    }

    override fun runTestMethod(testClassName: String, methodName: String, methodDescriptor: String): Monostate {
        try {
            executeTestMethodHandle.invoke(testClassName, methodName, methodDescriptor)
        } catch (ex: Exception) {
            throw exceptionNormalizer.normalize(ex)
        }

        return Monostate
    }

    override fun countOnlinePlayers(): Int {
        return Bukkit.getOnlinePlayers().size
    }

}
