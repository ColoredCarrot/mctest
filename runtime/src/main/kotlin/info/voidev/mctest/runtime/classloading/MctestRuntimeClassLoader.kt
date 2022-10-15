package info.voidev.mctest.runtime.classloading

import info.voidev.mctest.runtime.classloading.transform.CraftServerClassVisitor
import info.voidev.mctest.runtime.classloading.transform.JavaPluginClassVisitor
import info.voidev.mctest.runtime.classloading.transform.JavaPluginLoaderClassVisitor
import info.voidev.mctest.runtime.classloading.transform.NetworkManagerClassVisitor
import info.voidev.mctest.runtime.classloading.transform.transformClassFileBytes
import info.voidev.mctest.runtimesdk.proto.EngineService
import org.objectweb.asm.ClassVisitor
import java.io.File
import java.io.InputStream
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path

/**
 * Main class loader used while the server is active.
 * Instantiated in [org.bukkit.craftbukkit.bootstrap.Main].
 */
class MctestRuntimeClassLoader(urls: Array<URL>, parent: ClassLoader?) : URLClassLoader("MCTest-Runtime", urls, null) {

    private val engine = engineService!!

    init {
        engineService = null
        instance = this

        setUpApplicationClassLoading()
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        if (name.startsWith("info.voidev.mctest.runtime.activeserver.")) {
            synchronized(getClassLoadingLock(name)) {
                findLoadedClass(name)?.also { return it }

                val c = findClass(name)
                if (resolve) resolveClass(c)

                return c
            }
        }

        return super.loadClass(name, resolve)
    }

    override fun findClass(name: String): Class<*> {
        if (name.startsWith("java.sql.")) {
            throw ClassNotFoundException(name)
        }

        getClassTransformer(name)?.let { return findAndTransformClassInJars(name, it) }

        try {
            return super.findClass(name)
        } catch (_: ClassNotFoundException) {
            // Thrown if the class is not in the server JAR (and isn't a java.* class)
        }

        // FIXME Remove
        if (name.startsWith("info.voidev.mctest.engine.")) {
            throw ClassNotFoundException(name)
        }

        val bytes = engine.getTesteeClass(name)
            ?: throw ClassNotFoundException(name)

        return defineClass(name, bytes, 0, bytes.size)
    }

    private fun getClassTransformer(name: String): ((ClassVisitor) -> ClassVisitor)? {
        return when {
            name == "org.bukkit.plugin.java.JavaPlugin" -> ::JavaPluginClassVisitor
            name == "org.bukkit.plugin.java.JavaPluginLoader" -> ::JavaPluginLoaderClassVisitor
            name.startsWith("org.bukkit.craftbukkit.") && name.endsWith(".CraftServer") -> ::CraftServerClassVisitor
            name == "net.minecraft.network.NetworkManager" -> ::NetworkManagerClassVisitor
            else -> null
        }
    }

    private fun findAndTransformClassInJars(name: String, classVisitorFactory: (ClassVisitor) -> ClassVisitor): Class<*> {
        val origBytes = getResourceAsStream(name.replace('.', '/') + ".class")!!.use(InputStream::readAllBytes)

        val transformedBytes = transformClassFileBytes(origBytes, classVisitorFactory, this)

        return defineClass(name, transformedBytes, 0, transformedBytes.size)
    }

    private fun setUpApplicationClassLoading() {
        /*
        Why do we not just make use of the default application class loader
        (jdk.internal.loader.ClassLoaders.AppClassLoader)?
        Because even classes in the runtime (specifically the activeserver classes)
        depend on classes only present in the server JARs,
        so even those classes must be loaded by *this* class loader.
        Otherwise, they would attempt to load their dependency classes
        through the application class loader, which would not find them.
         */

        val cp = System.getProperty("java.class.path")?.ifBlank { null } ?: ""

        cp.splitToSequence(File.pathSeparator)
            .filter { it.isNotEmpty() }
            .map { Path.of(it).toUri().toURL() }
            .forEach(this::addURL)
    }

    companion object {
        var engineService: EngineService? = null

        @JvmStatic
        lateinit var instance: MctestRuntimeClassLoader
    }
}
