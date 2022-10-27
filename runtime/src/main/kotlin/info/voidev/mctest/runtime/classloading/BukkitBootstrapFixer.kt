package info.voidev.mctest.runtime.classloading

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.util.CheckClassAdapter

/**
 * Instruments the bytecode of `org.bukkit.craftbukkit.bootstrap.Main#run`
 * so that its class loader uses the current class loader as parent.
 */
class BukkitBootstrapFixer {

    fun fix(original: ByteArray): ByteArray {
        val cr = ClassReader(original)
        val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)

        val cv = object : ClassVisitor(Opcodes.ASM9, CheckClassAdapter(cw)) {
//        val cv = object : ClassVisitor(Opcodes.ASM9, CheckClassAdapter(TraceClassVisitor(cw, PrintWriter(System.err)))) {
            override fun visitMethod(
                access: Int,
                name: String,
                descriptor: String,
                signature: String?,
                exceptions: Array<String>?,
            ): MethodVisitor? {
                var v = super.visitMethod(access, name, descriptor, signature, exceptions)
//                v = BukkitBootstrapPrintStackTraceMethodFixer(v)

                return when (name) {
                    "run" -> BukkitBootstrapRunMethodFixer(v)
                    else -> v
                }
            }
        }

        cr.accept(cv, ClassReader.SKIP_FRAMES)

        return cw.toByteArray()
    }

}

/**
 * Transform
 * `new URLClassLoader($stackTop);`
 * into
 * `new MctestRuntimeClassLoader($stackTop, this.getClass().getClassLoader());`
 */
private class BukkitBootstrapRunMethodFixer(mv: MethodVisitor?) : MethodVisitor(Opcodes.ASM9, mv) {
    override fun visitTypeInsn(opcode: Int, type: String) {
        if (opcode == Opcodes.NEW && type == "java/net/URLClassLoader") {
            super.visitTypeInsn(Opcodes.NEW, Type.getInternalName(MctestRuntimeClassLoader::class.java))

            return
        }

        super.visitTypeInsn(opcode, type)
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean,
    ) {
        if (name == "<init>" && owner == "java/net/URLClassLoader") {
            // 1.18.2 uses the constructor with just the URL array argument;
            // 1.18 passes the URLs as well as the current class' class loader's parent.
            val whichCtor = when (descriptor) {
                "([Ljava/net/URL;)V" -> URLClassLoaderConstructor.URL_ARRAY
                "([Ljava/net/URL;Ljava/lang/ClassLoader;)V" -> URLClassLoaderConstructor.URL_ARRAY_AND_CLASSLOADER
                else -> throw IllegalStateException("Call to unsupported URLClassLoader constructor")
            }

            if (whichCtor == URLClassLoaderConstructor.URL_ARRAY_AND_CLASSLOADER) {
                // We just loaded our class loader's parent class loader onto the stack;
                // drop that one as we will replace it with our own
                super.visitInsn(Opcodes.POP)
            }

            // Load current class loader onto stack (i.e. our bootstrap class loader)
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/Object",
                "getClass",
                "()Ljava/lang/Class;",
                false
            )
            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/Class",
                "getClassLoader",
                "()Ljava/lang/ClassLoader;",
                false
            )

            super.visitMethodInsn(
                opcode,
                Type.getInternalName(MctestRuntimeClassLoader::class.java),
                name,
                "([Ljava/net/URL;Ljava/lang/ClassLoader;)V",
                false
            )

            return
        }

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

    private enum class URLClassLoaderConstructor {
        URL_ARRAY,
        URL_ARRAY_AND_CLASSLOADER,
    }
}

private class BukkitBootstrapPrintStackTraceMethodFixer(mv: MethodVisitor?) : MethodVisitor(Opcodes.ASM9, mv) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean,
    ) {
        if (name == "sneakyThrow" && descriptor == "(Ljava/lang/Throwable;)V") {
            // Also call ex.printStackTrace

            // DUP the exception
            super.visitInsn(Opcodes.DUP)

            // Call printStackTrace
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable", "printStackTrace", "()V", false)

            // Sneaky throw
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)

            return
        }

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

}

// This old class used  new URLClassLoader(theUrls, currentClassLoader)
//private class BukkitBootstrapRunMethodFixer(mv: MethodVisitor) : MethodVisitor(Opcodes.ASM9, mv) {
//    override fun visitMethodInsn(
//        opcode: Int,
//        owner: String,
//        name: String,
//        descriptor: String,
//        isInterface: Boolean
//    ) {
//        if (name == "<init>" && owner == "java/net/URLClassLoader" && descriptor == "([Ljava/net/URL;)V") {
//            // Load parent classloader onto stack
//            super.visitVarInsn(Opcodes.ALOAD, 0)
//            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
//            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false)
//
//            // Call different URLClassLoader constructor to also pass parent
//            super.visitMethodInsn(opcode, owner, name, "([Ljava/net/URL;Ljava/lang/ClassLoader;)V", isInterface)
//
//            return
//        }
//
//        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
//    }
//}
