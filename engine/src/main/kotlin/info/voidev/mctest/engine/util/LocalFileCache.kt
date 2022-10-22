package info.voidev.mctest.engine.util

import org.apache.commons.codec.binary.Base32
import java.net.URI
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.outputStream

class LocalFileCache(private val dir: Path) {

    private val base32 = Base32()

    fun getCached(uri: URI, filename: String = getFilename(uri), forceRefresh: Boolean = false): Path {
        val localCopy = dir.resolve(filename)

        if (forceRefresh || !localCopy.exists()) {
            download(uri, localCopy)
        }

        return localCopy
    }

    private fun getFilename(uri: URI): String {
        var filename = base32.encodeAsString(uri.normalize().toString().encodeToByteArray())

        // If URI path ends in a filename extension (e.g. http://example.com/file.jar),
        // append that extension to the cached file's name
        val path = uri.path.orEmpty()
        val filenameExt = URI_PATH_PATTERN.find(path)?.groupValues?.get(1)
        if (filenameExt != null) {
            filename += ".$filenameExt"
        }

        return filename
    }

    private fun download(uri: URI, target: Path) {
        dir.createDirectories()

        uri.toURL().openStream().use { istream ->
            target.outputStream(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { ostream ->
                istream.transferTo(ostream)
            }
        }
    }

    companion object {
        private val URI_PATH_PATTERN = Regex("""[^.]\.(\w{1,16})\z""")
    }
}
