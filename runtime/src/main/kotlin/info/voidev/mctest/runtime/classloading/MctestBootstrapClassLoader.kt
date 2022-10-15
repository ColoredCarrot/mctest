package info.voidev.mctest.runtime.classloading

import java.net.URLClassLoader
import java.nio.file.Path

/**
 * Class loader used to load [org.bukkit.craftbukkit.bootstrap.Main]
 * from the server JAR.
 */
class MctestBootstrapClassLoader(
    serverJar: Path,
    parent: ClassLoader?,
) : URLClassLoader("MCTest-Bootstrap", arrayOf(serverJar.toUri().toURL()), parent) {

    override fun findClass(name: String): Class<*> {
        if (name == "org.bukkit.craftbukkit.bootstrap.Main") {
            val path = name.replace('.', '/') + ".class"
            val originalBytes = findResource(path).readBytes()
            val transformedBytes = BukkitBootstrapFixer().fix(originalBytes)

            return defineClass(name, transformedBytes, 0, transformedBytes.size)
        }

        return super.findClass(name)
    }

}
