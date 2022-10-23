package info.voidev.mctest.engine.server

import info.voidev.mctest.engine.config.MCTestConfigException
import info.voidev.mctest.engine.proto.EngineServiceImpl
import info.voidev.mctest.engine.util.LocalFileCache
import info.voidev.mctest.engine.util.TemporaryDirectories
import info.voidev.mctest.engine.util.startDaemon
import info.voidev.mctest.runtimesdk.proto.EngineService
import info.voidev.mctest.runtimesdk.proto.MctestConfig
import info.voidev.mctest.runtimesdk.proto.RuntimeService
import java.nio.file.Path
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import kotlin.io.path.absolutePathString
import kotlin.io.path.div
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

class TestableMinecraftServer(private val config: MctestConfig) {

    private val serverDir = config.serverDirectory ?: TemporaryDirectories.create("mctest-server-")

    private var session: TestableServerSession? = null

    fun requireActiveSession() = session!!

    @Synchronized
    fun start() {
        if (session != null) {
            return
        }

        val pluginYml = PluginYmlParser().parseFromClasspath()

        MinecraftServerInitializer(serverDir).run()

        val rmiRegistry = LocateRegistry.createRegistry(config.rmiPort)
        val engineService = EngineServiceImpl(config)

        rmiRegistry.bind(EngineService.NAME, UnicastRemoteObject.exportObject(engineService, 0))

        val runtimeJar = findRuntimeJar().toAbsolutePath()
        validateJar(runtimeJar, "Runtime JAR")

        val serverJar = findServerJar().toAbsolutePath()
        validateJar(serverJar, "Server JAR")

        val process = ProcessBuilder(
            config.java.absolutePathString(),
            *config.runtimeJvmArgs.toTypedArray(),
//            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
//            "-javaagent:${runtimeJar.absolutePathString()}",
            "-ea",
            "-jar", runtimeJar.absolutePathString(),
            serverJar.absolutePathString(),
            config.rmiPort.toString(),
            pluginYml.name,
        )
            .directory(serverDir.toFile())
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .startDaemon()

        process.onExit().thenAcceptAsync { proc ->
            val exitCode = proc.exitValue()
            engineService.notifyRuntimeDidExit(exitCode)
        }

        // Wait until the runtime has bootstrapped the runtime service via RMI
        engineService.awaitBootstrap()

        // Get RMI entrypoint object
        val runtimeService = rmiRegistry.lookup(RuntimeService.NAME) as RuntimeService

        session = TestableServerSession(process, runtimeService, engineService)
    }

    @Synchronized
    fun stop() {
        session?.also { session ->
            session.process.destroy()

            this.session = null
        }
    }

    private fun findRuntimeJar(): Path {
        // An explicitly configured JAR always takes precedence
        config.runtimeJar?.also { return it }

        // Might be bundled, i.e. on our classpath
        javaClass.classLoader.getResource("runtime.jar")?.also { url ->
            val cache = LocalFileCache(config.dataDirectory / "runtime")
            return cache.getCached(url.toURI(), "runtime.jar", forceRefresh = true)
        }

        // TODO download runtime JAR in case this is a "light engine", i.e. without the bundled runtime
        throw MCTestConfigException("Missing runtime.jar. Configure it via mctest.runtime.jar or use an engine with bundled runtime.")
    }

    private fun findServerJar(): Path {
        val downloadUri = config.downloadableServerJar ?: ServerJarGetter.get()

        return LocalFileCache(config.serverJarCacheDirectory).getCached(downloadUri)
    }

    private fun validateJar(path: Path, name: String) {
        if (!path.extension.equals("jar", ignoreCase = true)) {
            throw MCTestConfigException("$name is not a JAR file: $path")
        }
        if (!path.isRegularFile()) {
            throw MCTestConfigException("$name does not exist or is not a regular file: $path")
        }
    }
}
