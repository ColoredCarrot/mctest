package info.voidev.mctest.runtimesdk.proto

import info.voidev.mctest.runtimesdk.versioning.VersionMatrixStrategy
import java.net.URI
import java.nio.file.Path

interface MctestConfig {

    /**
     * Absolute path to the java executable used for the Minecraft server.
     */
    val java: Path

    /**
     * Absolute path to the MCTest data directory.
     *
     * This directory is used for a multitude of files
     * that should persist across test runs, including:
     * - the default server JAR cache, and
     * - the default runtime JAR cache.
     *
     * ~100MB are sufficient to store one copy of the runtime JAR
     * as well as one Spigot server JAR.
     */
    val dataDirectory: Path

    /**
     * Absolute path to the MCTest runtime JAR.
     *
     * If `null`, the classpath will be searched for the runtime
     * and it will be cached in the data directory.
     */
    val runtimeJar: Path?

    /**
     * The version of the Minecraft server to install and use.
     *
     * If `null`, an appropriate version will be determined automatically.
     *
     * Example values: `1.19`, `1.18.2`
     */
    val minecraftVersion: String?

    val minecraftVersionStrategy: VersionMatrixStrategy

    /**
     * URL that the Minecraft server JAR can be downloaded from.
     *
     * Can be an `http(s)://`, a `file://`, or any other kind of URL.
     *
     * Setting this value overrides any auto-detection mechanisms.
     */
    val downloadableServerJar: URI?

    /**
     * Absolute path to the directory used to cache server JARs.
     */
    val serverJarCacheDirectory: Path

    /**
     * Absolute path to a directory under which the Minecraft server will be run.
     *
     * The actual server directory is some subdirectory of this directory.
     *
     * If `null`, a temporary directory created by the operating system will be used.
     */
    val serverDirectory: Path?

    /**
     * The port to use for RMI communication between runtime and engine.
     */
    val rmiPort: Int

    /**
     * Additional JVM arguments passed to the runtime.
     */
    val runtimeJvmArgs: List<String>

    /**
     * Timeout in milliseconds during which the runtime must finish bootstrapping.
     *
     * Bootstrapping involves setting up bidirectional communication between runtime and engine
     * and performing some basic required configurations,
     * but does **not** include starting the Minecraft server.
     */
    val runtimeBootstrapTimeoutMs: Long

    /**
     * Timeout in milliseconds during which the Minecraft server must finish booting.
     *
     * A booted server is ready to execute test methods.
     *
     * Do not set this timeout too low, since a cold server boot
     * involves unbundling Minecraft libraries, generating worlds, etc.,
     * which can take multiple minutes.
     */
    val serverStartTimeoutMs: Long

    /**
     * Timeout in milliseconds between a test player connecting
     * and them appearing on the server (via `Bukkit.getPlayerExact()`).
     */
    val testPlayerJoinTimeoutMs: Long

    /**
     * Timeout in milliseconds during which the runtime must exit.
     *
     * This timeout is the last resort to terminate the runtime even if
     * all other mechanisms of detecting the engine's death fail.
     * Therefore, this value should have a rather high value (minutes to an hour).
     */
    val runtimeGlobalTimeoutMs: Long
}
