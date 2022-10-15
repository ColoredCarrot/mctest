package info.voidev.mctest.engine.config

import info.voidev.mctest.engine.util.SystemInfo
import org.junit.platform.engine.ConfigurationParameters
import java.nio.file.Path
import kotlin.io.path.div

class MCTestConfig(params: ConfigurationParameters) {

    val java: Path = params
        .get("mctest.java", Path::of).orElse(null)
        ?: Path.of("C:\\Users\\Koch\\.jdks\\openjdk-18.0.1.1\\bin\\java") // TODO: Discover java dynamically
        ?: throw MCTestConfigException()

    val dataDirectory: Path = params
        .get("mctest.data.dir", Path::of).orElse(null)
        ?: getDefaultDataDir()
        ?: throw MCTestConfigException()

    val serverJarCacheDirectory: Path = params
        .get("mctest.server.jar.cache", Path::of).orElse(null)
        ?: (dataDirectory / "serverjars")

    val serverDirectory: Path? = params
        .get("mctest.server.dir").orElse(null)
        ?.let { if (it == "TEMP" || it == "TMP" || it.isEmpty()) null else Path.of(it) }

    val rmiPort: Int = params
        .get("mctest.rmi.port", String::toInt).orElse(null)
        ?: 1099

    val runtimeBootstrapTimeoutMs: Long = params
        .get("mctest.runtime.bootstrap.timeout.ms", String::toLong).orElse(null)
        ?: (10 * 1000L)

    val serverStartTimeoutMs: Long = params
        .get("mctest.server.start.timeout.ms", String::toLong).orElse(null)
        ?: (120 * 1000L)

    fun export() = mapOf(
        "java" to java.toString(),
        "dataDirectory" to dataDirectory.toString(),
        "serverJarCacheDirectory" to serverJarCacheDirectory.toString(),
        "serverDirectory" to serverDirectory.toString(),
        "rmiPort" to rmiPort.toString(),
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
