package info.voidev.mctest.engine.util

import java.nio.file.Files
import java.nio.file.Path
import java.util.Collections

object TemporaryDirectories {

    private val pathsToDelete = Collections.synchronizedList(ArrayList<Path>())

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
