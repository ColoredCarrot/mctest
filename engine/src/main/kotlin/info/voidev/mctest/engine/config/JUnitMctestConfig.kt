package info.voidev.mctest.engine.config

import info.voidev.mctest.engine.util.SystemInfo
import info.voidev.mctest.runtimesdk.proto.MctestConfig
import org.junit.platform.engine.ConfigurationParameters
import java.nio.file.Path
import kotlin.io.path.div

class JUnitMctestConfig(params: ConfigurationParameters) : MctestConfig {

    override val java: Path = params
        .get("mctest.java").orElse(null)
        ?.ifEmpty { null }
        ?.let(Path::of)
        ?: SystemInfo.findJava()
        ?: throw MCTestConfigException("mctest.java")

    override val dataDirectory: Path = params
        .get("mctest.data.dir", Path::of).orElse(null)
        ?: getDefaultDataDir()
        ?: throw MCTestConfigException("mctest.data.dir")

    override val runtimeJar: Path? = params
        .get("mctest.runtime.jar").orElse(null)
        ?.ifEmpty { null }
        ?.let(Path::of)

    override val serverJarCacheDirectory: Path = params
        .get("mctest.server.jar.cache", Path::of).orElse(null)
        ?: (dataDirectory / "serverjars")

    override val serverDirectory: Path? = params
        .get("mctest.server.dir").orElse(null)
        ?.let { if (it == "TEMP" || it == "TMP" || it.isEmpty()) null else Path.of(it) }

    override val rmiPort: Int = params
        .get("mctest.rmi.port", String::toInt).orElse(null)
        ?: 1099

    override val runtimeBootstrapTimeoutMs: Long = params
        .get("mctest.runtime.bootstrap.timeout.ms", String::toLong).orElse(null)
        ?: (10 * 1000L)

    override val serverStartTimeoutMs: Long = params
        .get("mctest.server.start.timeout.ms", String::toLong).orElse(null)
        ?: (120 * 1000L)

    fun export(): Map<String, String> = mapOf(
        "mctest.java" to java.toString(),
        "mctest.data.dir" to dataDirectory.toString(),
        "mctest.runtime.jar" to runtimeJar.toString(),
        "mctest.server.jar.cache" to serverJarCacheDirectory.toString(),
        "mctest.server.dir" to serverDirectory.toString(),
        "mctest.rmi.port" to rmiPort.toString(),
        "mctest.runtime.bootstrap.timeout.ms" to runtimeBootstrapTimeoutMs.toString(),
        "mctest.server.start.timeout.ms" to serverStartTimeoutMs.toString(),
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
}
