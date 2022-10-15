package info.voidev.mctest.runtime.classloading.transform

import info.voidev.mctest.runtime.activeserver.ServerStartCallback
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type

/**
 * Inserts a call to [ServerStartCallback.afterEnablePlugins]
 * into `CraftServer.enablePlugins`.
 */
class CraftServerClassVisitor(cv: ClassVisitor?) : ClassVisitor(ASM9, cv) {

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions) ?: return null

        return when {
            name == "enablePlugins" && descriptor == "(Lorg/bukkit/plugin/PluginLoadOrder;)V" -> EnablePluginsMethodVisitor(mv)
            else -> mv
        }
    }

    private class EnablePluginsMethodVisitor(mv: MethodVisitor) : MethodVisitor(ASM9, mv) {
        override fun visitInsn(opcode: Int) {
            if (opcode == RETURN) {
                super.visitVarInsn(ALOAD, 1)
                super.visitMethodInsn(
                    INVOKESTATIC,
                    Type.getInternalName(ServerStartCallback::class.java),
                    "afterEnablePlugins",
                    "(Lorg/bukkit/plugin/PluginLoadOrder;)V",
                    false
                )
            }

            super.visitInsn(opcode)
        }
    }
}
