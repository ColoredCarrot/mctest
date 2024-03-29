package info.voidev.mctest.engine.server

import info.voidev.mctest.engine.config.MCTestConfigException
import info.voidev.mctest.engine.proto.EngineServiceImpl
import info.voidev.mctest.engine.server.platform.MalformedVersionException
import info.voidev.mctest.engine.server.platform.MinecraftPlatform
import info.voidev.mctest.engine.util.LocalFileCache
import info.voidev.mctest.engine.util.TemporaryDirectories
import info.voidev.mctest.engine.util.startDaemon
import info.voidev.mctest.runtimesdk.proto.EngineService
import info.voidev.mctest.runtimesdk.proto.MctestConfig
import info.voidev.mctest.runtimesdk.proto.RuntimeService
import java.nio.file.Path
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.io.path.absolutePathString
import kotlin.io.path.div
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class TestableMinecraftServer<V : MinecraftPlatform.Version<V>>(
    private val config: MctestConfig,
    private val minecraftPlatform: MinecraftPlatform<V>,
    private val allowableVersionRange: Pair<V?, V?>,
) {

    private val serverDir = config.serverDirectory ?: TemporaryDirectories.create("mctest-server-")

    private var session: TestableServerSession? = null

    private val pluginYml = PluginYmlParser().parseFromClasspath()

    fun requireActiveSession() = session!!

    @Synchronized
    fun start() {
        if (session != null) {
            return
        }

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
        var downloadUri = config.downloadableServerJar
        var filename: String? = null

        // No specifically configured download URL?
        if (downloadUri == null) {
            val version = determineMinecraftVersion()
            val installer = minecraftPlatform.availableInstallers.first() // TODO: Make installer configurable; use a retry mechanism/try different installers

            downloadUri = logTimeElapsed(
                message = { uri, took -> "Installed $minecraftPlatform $version via $installer in $took. Resolved to: $uri" },
                block = { installer.install(version) },
            )
            filename = version.filename
        }

        return logTimeElapsed({ localJar, took -> "Downloaded $downloadUri to $localJar in $took." }) {
            LocalFileCache(config.serverJarCacheDirectory).getCached(downloadUri, filename)
        }
    }

    private fun determineMinecraftVersion(): V {
        var (min, max) = allowableVersionRange

        // Consider plugin.yml
        if (pluginYml.apiVersion != null) {
            val declaredApiVersion = minecraftPlatform.resolveVersion(pluginYml.apiVersion)
            if (min == null || declaredApiVersion > min) {
                min = declaredApiVersion
            }
        }

        // Determine from config
        val configuredVersion = try {
            config.minecraftVersion?.let(minecraftPlatform::resolveVersion)
        } catch (ex: MalformedVersionException) {
            throw RuntimeException("Configured Minecraft version is invalid: ${ex.message}", ex)
        }
        configuredVersion?.also { version ->
            if (min != null && version < min || max != null && version > max) {
                throw RuntimeException("Configured Minecraft version ($version) conflicts with inferred allowable version range (${min ?: "*"} - ${max ?: "*"})")
            }

            return version
        }

        // Determine from @MCVersion annotations
        max?.also { return it }
        min?.also { return it }

        // No way to infer a version
        log.warning(
            """
            Failed to infer a Minecraft version; using platform default (${minecraftPlatform.defaultVersion}).
              Please consider specifying a version by declaring an api-version in your plugin.yml,
              annotating at least one test or test class with @MCVersion, or
              setting `mctest.server.version` as a JUnit Platform configuration parameter.
        """.trimIndent()
        )

        return minecraftPlatform.defaultVersion
    }

    private fun validateJar(path: Path, name: String) {
        if (!path.extension.equals("jar", ignoreCase = true)) {
            throw MCTestConfigException("$name is not a JAR file: $path")
        }
        if (!path.isRegularFile()) {
            throw MCTestConfigException("$name does not exist or is not a regular file: $path")
        }
    }

    private inline fun <R> logTimeElapsed(message: (R, Duration) -> String, block: () -> R): R {
        val startMs = System.currentTimeMillis()
        val ret = block()
        val took = (System.currentTimeMillis() - startMs).milliseconds

        log.log(if (took > 2.seconds) Level.INFO else Level.CONFIG, message(ret, took))

        return ret
    }

    companion object {
        private val log = Logger.getLogger(TestableMinecraftServer::class.java.name)
    }
}
