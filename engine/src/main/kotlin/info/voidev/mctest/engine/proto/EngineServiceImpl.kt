package info.voidev.mctest.engine.proto

import info.voidev.mctest.engine.config.MCTestConfig
import info.voidev.mctest.runtimesdk.proto.EngineService
import info.voidev.mctest.runtimesdk.util.IsServerClassName
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class EngineServiceImpl(private val config: MCTestConfig) : EngineService {

    private val bootstrapMutex = CountDownLatch(1)
    private val serverStartedMutex = CountDownLatch(1)

    override fun notifyBootstrapComplete() {
        bootstrapMutex.countDown()
    }

    override fun notifyServerIsRunning() {
        serverStartedMutex.countDown()
    }

    fun awaitBootstrap() {
        val finished = bootstrapMutex.await(config.runtimeBootstrapTimeoutMs, TimeUnit.MILLISECONDS)
        if (!finished) {
            //TODO find a better design that an exception here
            throw IllegalStateException("Runtime bootstrapping timed out")
        }
    }

    fun waitForServerToStart() {
        val finished = serverStartedMutex.await(config.serverStartTimeoutMs, TimeUnit.MILLISECONDS)
        if (!finished) {
            //TODO find a better design that an exception here
            throw IllegalStateException("Runtime bootstrapping timed out")
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

}
