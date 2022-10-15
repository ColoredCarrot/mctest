package info.voidev.mctest.runtime.classloading.transform

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

/**
 * Visitor for [org.bukkit.plugin.java.JavaPlugin] that
 * drops all code from the no-args constructor.
 */
class JavaPluginClassVisitor(cv: ClassVisitor?) : ClassVisitor(ASM9, cv) {

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions) ?: return null

        return when {
            name == "<init>" && descriptor == "()V" -> ConstructorVisitor(mv)
            else -> mv
        }
    }

    private class ConstructorVisitor(private val target: MethodVisitor) : MethodVisitor(ASM9) {
        override fun visitCode() {
            target.visitCode()

            // Call parent constructor
            target.visitVarInsn(ALOAD, 0)
            target.visitMethodInsn(INVOKESPECIAL, "org/bukkit/plugin/PluginBase", "<init>", "()V", false)
            target.visitInsn(RETURN)

            target.visitMaxs(1, 1)
            target.visitEnd()
        }
    }
}
