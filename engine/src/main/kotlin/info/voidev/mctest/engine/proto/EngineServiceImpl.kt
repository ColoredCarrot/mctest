package info.voidev.mctest.engine.proto

import info.voidev.mctest.engine.server.RuntimeDidExitException
import info.voidev.mctest.runtimesdk.RuntimeExitCodes
import info.voidev.mctest.runtimesdk.proto.EngineService
import info.voidev.mctest.runtimesdk.proto.MctestConfig
import info.voidev.mctest.runtimesdk.util.IsServerClassName
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class EngineServiceImpl(private val config: MctestConfig) : EngineService {

    private val bootstrapMutex = CountDownLatch(1)
    private val serverStartedMutex = CountDownLatch(1)
    @Volatile
    private var runtimeExitCode = RuntimeExitCodes.RESERVED_NO_EXIT

    override fun notifyBootstrapComplete() {
        bootstrapMutex.countDown()
    }

    override fun notifyServerIsRunning() {
        serverStartedMutex.countDown()
    }

    fun notifyRuntimeDidExit(code: Int) {
        runtimeExitCode = code
        bootstrapMutex.countDown()
        serverStartedMutex.countDown()
    }

    fun awaitBootstrap() {
        val finished = bootstrapMutex.await(config.runtimeBootstrapTimeoutMs, TimeUnit.MILLISECONDS)
        if (!finished) {
            //TODO find a better design that an exception here
            throw IllegalStateException("Runtime bootstrapping timed out")
        }
        if (runtimeExitCode != RuntimeExitCodes.RESERVED_NO_EXIT) {
            throw RuntimeDidExitException(runtimeExitCode)
        }
    }

    fun waitForServerToStart() {
        val finished = serverStartedMutex.await(config.serverStartTimeoutMs, TimeUnit.MILLISECONDS)
        if (!finished) {
            //TODO find a better design that an exception here
            throw IllegalStateException("Runtime bootstrapping timed out")
        }
        if (runtimeExitCode != RuntimeExitCodes.RESERVED_NO_EXIT) {
            throw RuntimeDidExitException(runtimeExitCode)
        }
    }

    override fun getTesteeClass(name: String): ByteArray? {
        // We are never able to load Bukkit or Minecraft classes over the wire;
        // this is a performance optimization and also allows us in development environments
        // to have such classes on the engine's classpath.
        if (IsServerClassName.test(name)) {
            return null
        }

        return javaClass.classLoader.getResourceAsStream(
            name.replace('.', '/') + ".class"
        )?.use(InputStream::readAllBytes)
    }

    override fun maySendClass(name: String): Boolean {
        return javaClass.classLoader.getResource(name.replace('.', '/') + ".class") != null
    }

    override fun getConfiguration(): MctestConfig {
        return config
    }
}
