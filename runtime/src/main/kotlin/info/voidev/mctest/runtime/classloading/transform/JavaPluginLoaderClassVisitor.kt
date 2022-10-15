package info.voidev.mctest.runtime.classloading.transform

import info.voidev.mctest.runtime.classloading.MctestRuntimeClassLoader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type

/*
Rewrite:
PluginClassLoader: jarClass = Class.forName(description.getMain(), true, this);

JavaPluginLoader:

Manually call plugin.init(...)

enablePlugin(...):
Drop the following code:
    PluginClassLoader pluginLoader = (PluginClassLoader) jPlugin.getClassLoader();
    if (!loaders.contains(pluginLoader)) {
        loaders.add(pluginLoader);
        server.getLogger().log(Level.WARNING, "Enabled plugin with unregistered PluginClassLoader " + plugin.getDescription().getFullName());
    }

JavaPlugin no-args constructor:
Drop all code inside it
 */
class JavaPluginLoaderClassVisitor(cv: ClassVisitor?) : ClassVisitor(ASM9, cv) {

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?,
    ): MethodVisitor? {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions) ?: return null

        return when {
            name == "loadPlugin" -> LoadPluginMethodVisitor(mv)
            name == "enablePlugin" -> EnablePluginMethodVisitor(mv)
            else -> mv
        }
    }

    private class LoadPluginMethodVisitor(mv: MethodVisitor) : MethodVisitor(ASM9, mv) {
        override fun visitInsn(opcode: Int) {
            if (opcode == ARETURN) {
                // Insert a call to init(...) on the plugin,
                // because we removed that call from within the JavaPlugin constructor
                super.visitInsn(DUP)

                // Put the arguments on the stack:
                // 1. PluginLoader: this
                super.visitVarInsn(ALOAD, 0)

                // 2. Server
                super.visitInsn(DUP)
                super.visitFieldInsn(GETFIELD, "org/bukkit/plugin/java/JavaPluginLoader", "server", "Lorg/bukkit/Server;")

                // 3. PluginDescriptionFile: This is a local variable
                // TODO: Using the hardcoded index here is very fragile; discover the index by scanning for the getPluginDescription() call
                super.visitVarInsn(ALOAD, 2)

                // 4. File: data folder
                super.visitVarInsn(ALOAD, 4)

                // 5. File: the first and only argument passed to loadPlugin()
                super.visitVarInsn(ALOAD, 1)

                // 6. ClassLoader: Our MctestRuntimeClassLoader
                visitFieldInsn(GETSTATIC, Type.getInternalName(MctestRuntimeClassLoader::class.java), "instance", Type.getDescriptor(MctestRuntimeClassLoader::class.java))

                super.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "org/bukkit/plugin/java/JavaPlugin",
                    "init",
                    "(Lorg/bukkit/plugin/PluginLoader;Lorg/bukkit/Server;Lorg/bukkit/plugin/PluginDescriptionFile;Ljava/io/File;Ljava/io/File;Ljava/lang/ClassLoader;)V",
                    false
                )
            }

            super.visitInsn(opcode)
        }
    }

    private class EnablePluginMethodVisitor(mv: MethodVisitor) : MethodVisitor(ASM9, mv) {
        override fun visitTypeInsn(opcode: Int, type: String) {
            when {
                opcode == CHECKCAST && type == "org/bukkit/plugin/java/PluginClassLoader" -> { /*Drop the cast*/ }
                else -> super.visitTypeInsn(opcode, type)
            }
        }
    }
}
