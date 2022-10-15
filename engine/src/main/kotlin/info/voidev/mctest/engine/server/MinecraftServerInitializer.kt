package info.voidev.mctest.engine.server

import info.voidev.mctest.api.AssertionFailedException
import info.voidev.mctest.engine.util.deleteRecursively
import info.voidev.mctest.engine.util.isEmptyDirectory
import info.voidev.mctest.runtimesdk.ServerInitException
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.outputStream

/**
 * Initializes a directory for use by a testable Minecraft server instance.
 */
class MinecraftServerInitializer(private val dir: Path) : Runnable {

    override fun run() {
        validateDirectory()
        dir.createDirectories()

        dir.resolve("plugins").deleteRecursively()

        // Copy files from resources/server-init into server directory
        for (filePath in SERVER_INIT_FILES) {
            Thread.currentThread().contextClassLoader.getResourceAsStream("server-init/$filePath")!!.use { istream ->
                val fileOnDisk = dir / filePath
                fileOnDisk.parent.createDirectories()
                Files.newOutputStream(fileOnDisk, CREATE, TRUNCATE_EXISTING).use { ostream ->
                    istream.transferTo(ostream)
                }
            }
        }

        // TODO: Paste a very simple, small standard world
        //  so the server doesn't have to spend time generating one

        buildVirtualPlugin()
    }

    /**
     * Builds a small plugin JAR with the plugin.yml from our classpath
     * (no classes needed in it; they will be loaded over the wire by the CrossJvmClassLoader).
     */
    private fun buildVirtualPlugin() {
        val pluginJar = dir
            .resolve("plugins")
            .createDirectories()
            .resolve("testee.jar")

        JarOutputStream(
            pluginJar
                .outputStream(CREATE, TRUNCATE_EXISTING)
                .buffered()
        ).use { ostream ->
            ostream.putNextEntry(ZipEntry("plugin.yml"))
            buildVirtualPluginYml(ostream)
            ostream.closeEntry()
        }
    }

    private fun buildVirtualPluginYml(ostream: OutputStream) {
        // Find the actual testee's plugin.yml, which will be on our classpath
        // (since a project's test classpath should be a superset of the main classpath)

        val actualPluginYml = javaClass.classLoader.getResourceAsStream("plugin.yml")
            ?: throw AssertionFailedException("plugin.yml is not on the classpath")

        actualPluginYml.transferTo(ostream)
    }

    private fun validateDirectory() {
        if (!dir.exists() || dir.isEmptyDirectory()) {
            // Perfect, it's a fresh directory
            return
        }

        if (dir.exists() && !dir.isDirectory()) {
            throw ServerInitException("Server directory is not a directory: $dir")
        }

        // A previous directory exists; check that it is likely a server directory
        if (!dir.resolve("server.properties").isRegularFile() ||
            !dir.resolve("bukkit.yml").isRegularFile() ||
            !dir.resolve("bundler").isDirectory()
        ) {
            throw ServerInitException("Existing server directory does not look like a server directory: $dir")
        }
    }

    companion object {
        private val SERVER_INIT_FILES = listOf(
            "eula.txt",
            "server.properties",
            "spigot.yml",
            "plugins/PluginMetrics/config.yml",
        )
    }
}
