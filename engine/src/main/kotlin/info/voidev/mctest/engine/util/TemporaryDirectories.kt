package info.voidev.mctest.engine.util

import java.nio.file.Files
import java.nio.file.Path

object TemporaryDirectories {

    private val pathsToDelete = ArrayList<Path>()

    fun create(prefix: String): Path = Files
        .createTempDirectory(prefix)
        .toAbsolutePath()
        .also(pathsToDelete::add)

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            for (path in pathsToDelete) {
                path.deleteRecursively()
            }
        })
    }
}
