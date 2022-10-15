package info.voidev.mctest.engine.util

object SystemInfo {

    private val osName = System.getProperty("os.name")?.lowercase() ?: "unknown"

    val isWindows = osName.startsWith("windows")

    val isMac = osName.startsWith("mac")

    val isUnix = !isWindows

}
