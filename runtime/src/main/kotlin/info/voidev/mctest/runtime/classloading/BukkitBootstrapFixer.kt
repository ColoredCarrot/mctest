package info.voidev.mctest.runtime.classloading

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.util.CheckClassAdapter
import org.objectweb.asm.util.TraceClassVisitor
import java.io.PrintWriter

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
        if (name == "<init>" && owner == "java/net/URLClassLoader" && descriptor == "([Ljava/net/URL;)V") {
            // Load parent classloader onto stack
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
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
