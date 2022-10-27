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
class MctestRuntimeClassLoader(
    /**
     * URLs to the server JAR and its libraries, populated during Bukkit's bootstrapping.
     */
    urls: Array<URL>,

    /**
     * The class loader with which Bukkit's bootstrap Main was loaded, i.e. the [MctestBootstrapClassLoader].
     * Used to load Java platform classes that are not visible to the Java boot class loader, like `java.sql.*`.
     *
     * This class loader is **not** passed as our parent to [URLClassLoader];
     * we want to have full control over when it is used.
     */
    private val theParent: ClassLoader,
) : URLClassLoader("MCTest-Runtime", urls, null) {

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
        getClassTransformer(name)?.let { transformer ->
            return findAndTransformClassInJars(name, transformer)
        }

        try {
            return super.findClass(name)
        } catch (_: ClassNotFoundException) {
            // Thrown if the class is not in the server JAR or its libraries and isn't found by the Java boot class loader
        }

        // Since Java 9, many Java modules that used to be visible to the boot class loader, like java.sql.*,
        // have moved such that they are visible only to the platform class loader
        // (not the application class loader either--they're not on the classpath).
        // Therefore, we need to ask the platform class loader as well, which is accessible to us via our parent.
        // See https://bugs.openjdk.org/browse/JDK-8161269?focusedCommentId=13973088&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-13973088
        try {
            return theParent.loadClass(name)
        } catch (_: ClassNotFoundException) {
            // Thrown if the class is not a Java platform class
        }

        // Optimization/Security Guard: We never want to access engine classes in the runtime
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
        through the application class loader, which would not find them,
        because they could be needed to be fetched from the engine.
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
