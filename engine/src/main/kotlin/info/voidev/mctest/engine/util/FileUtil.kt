package info.voidev.mctest.engine.util

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

fun Path.isEmptyDirectory(): Boolean {
    if (!isDirectory()) {
        return false
    }

    return Files.newDirectoryStream(this).use {
        !it.iterator().hasNext()
    }
}

fun Path.deleteRecursively() {
    if (exists()) {
        Files.walkFileTree(this, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                if (exc != null) throw exc

                Files.delete(dir)
                return FileVisitResult.CONTINUE
            }
        })
    }
}
