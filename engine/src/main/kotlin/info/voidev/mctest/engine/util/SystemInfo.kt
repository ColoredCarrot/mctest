package info.voidev.mctest.engine.util

import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.isExecutable
import kotlin.io.path.isRegularFile

object SystemInfo {

    private val osName = System.getProperty("os.name")?.lowercase() ?: "unknown"

    val isWindows = osName.startsWith("windows")

    val isMac = osName.startsWith("mac")

    val isUnix = !isWindows

    fun findJava(): Path? {
        val javaHome = System.getenv("JAVA_HOME")?.trim()?.ifEmpty { null }?.let(Path::of)
            ?: return null

        val javaExecutableName = if (isWindows) "java.exe" else "java"
        val java = javaHome / "bin" / javaExecutableName

        if (!java.isRegularFile() || !java.isExecutable()) {
            return null
        }

        return java
    }

}
