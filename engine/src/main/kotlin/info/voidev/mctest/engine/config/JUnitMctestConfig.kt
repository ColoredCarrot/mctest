package info.voidev.mctest.engine.config

import info.voidev.mctest.engine.util.SystemInfo
import info.voidev.mctest.runtimesdk.proto.MctestConfig
import info.voidev.mctest.runtimesdk.proto.MctestConfigDto
import org.junit.platform.engine.ConfigurationParameters
import java.io.ObjectStreamException
import java.io.Serializable
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.io.path.div

class JUnitMctestConfig(params: ConfigurationParameters) : MctestConfig, Serializable {

    override val java = params
        .get("mctest.java").orElse(null)
        ?.ifEmpty { null }
        ?.let(::Path)
        ?: SystemInfo.findJava()
        ?: throw MCTestConfigException("mctest.java")

    override val dataDirectory = params
        .get("mctest.data.dir", ::Path).orElse(null)
        ?: getDefaultDataDir()
        ?: throw MCTestConfigException("mctest.data.dir")

    override val runtimeJar = params
        .get("mctest.runtime.jar").orElse(null)
        ?.ifEmpty { null }
        ?.let(::Path)

    override val serverJarCacheDirectory = params
        .get("mctest.server.jar.cache", ::Path).orElse(null)
        ?: (dataDirectory / "serverjars")

    override val serverDirectory = params
        .get("mctest.server.dir").orElse(null)
        ?.takeUnless { it == "TEMP" || it == "TMP" || it.isEmpty() }
        ?.let(::Path)

    override val rmiPort: Int = params
        .get("mctest.rmi.port", String::toInt).orElse(null)
        ?: 1099

    override val runtimeBootstrapTimeoutMs: Long = params
        .get("mctest.runtime.bootstrap.timeout.ms", String::toLong).orElse(null)
        ?: (10 * 1000L)

    override val serverStartTimeoutMs: Long = params
        .get("mctest.server.start.timeout.ms", String::toLong).orElse(null)
        ?: (120 * 1000L)

    override val testPlayerJoinTimeoutMs: Long = params
        .get("mctest.testplayer.join.timeout.ms", String::toLong).orElse(null)
        ?: (10 * 1000L)

    override val runtimeGlobalTimeoutMs: Long = params
        .get("mctest.runtime.global.timeout.ms", String::toLong).orElse(null)
        ?: TimeUnit.MINUTES.toMillis(30)

    fun export(): Map<String, String> = mapOf(
        "mctest.java" to java.toString(),
        "mctest.data.dir" to dataDirectory.toString(),
        "mctest.runtime.jar" to runtimeJar.toString(),
        "mctest.server.jar.cache" to serverJarCacheDirectory.toString(),
        "mctest.server.dir" to serverDirectory.toString(),
        "mctest.rmi.port" to rmiPort.toString(),
        "mctest.runtime.bootstrap.timeout.ms" to runtimeBootstrapTimeoutMs.toString(),
        "mctest.server.start.timeout.ms" to serverStartTimeoutMs.toString(),
        "mctest.testplayer.join.timeout.ms" to testPlayerJoinTimeoutMs.toString(),
        "mctest.runtime.global.timeout.ms" to runtimeGlobalTimeoutMs.toString(),
    )

    override fun toString() = export()
        .entries
        .joinToString(prefix = "MCTestConfig[\n\t", separator = ",\n\t", postfix = "\n]") { (key, value) ->
            "$key\t= $value"
        }

    private fun getDefaultDataDir(): Path? {
        return when {
            SystemInfo.isWindows ->
                (System.getenv("LOCALAPPDATA") ?: System.getenv("APPDATA"))
                    ?.let { Path.of(it, "MCTest") }

            SystemInfo.isUnix ->
                System.getProperty("user.home")?.let { Path.of(it, ".mctest") }

            else -> null
        }
    }

    @Throws(ObjectStreamException::class)
    private fun writeReplace(): Any {
        return MctestConfigDto(
            java.toString(),
            dataDirectory.toString(),
            runtimeJar?.toString(),
            serverJarCacheDirectory.toString(),
            serverDirectory?.toString(),
            rmiPort,
            runtimeBootstrapTimeoutMs,
            serverStartTimeoutMs,
            testPlayerJoinTimeoutMs,
            runtimeGlobalTimeoutMs,
        )
    }
}
