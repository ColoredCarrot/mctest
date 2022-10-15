package info.voidev.mctest.runtime.classloading.transform

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

/**
 * Inserts a call to [info.voidev.mctest.runtime.activeserver.lib.packet.ServerPacketCounter.noticeClientboundPacket]
 * into [net.minecraft.network.NetworkManager.b]`(net.minecraft.network.protocol.Packet<?>, io.netty.util.concurrent.GenericFutureListener<? extends io.netty.util.concurrent.Future<? super java.lang.Void>>)`.
 */
class NetworkManagerClassVisitor(cv: ClassVisitor?) : ClassVisitor(ASM9, cv) {

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?,
    ): MethodVisitor? {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions) ?: return null

        return when {
            (access and ACC_PRIVATE) == ACC_PRIVATE &&
                    descriptor == "(Lnet/minecraft/network/protocol/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V" ->
                SendPacketMethodVisitor(mv)

            descriptor == "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V" ->
                ReceivePacketMethodVisitor(mv)

            else -> mv
        }
    }

    private class SendPacketMethodVisitor(mv: MethodVisitor) : MethodVisitor(ASM9, mv) {
        override fun visitCode() {
            super.visitCode()

            super.visitVarInsn(ALOAD, 0)
            super.visitVarInsn(ALOAD, 1)
            super.visitMethodInsn(
                INVOKESTATIC,
                "info/voidev/mctest/runtime/activeserver/lib/packet/ServerPacketCounter",
                "noticeClientboundPacket",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                false
            )
        }
    }

    private class ReceivePacketMethodVisitor(mv: MethodVisitor) : MethodVisitor(ASM9, mv) {
        override fun visitCode() {
            super.visitCode()

            super.visitVarInsn(ALOAD, 0)
            super.visitVarInsn(ALOAD, 2)
            super.visitMethodInsn(
                INVOKESTATIC,
                "info/voidev/mctest/runtime/activeserver/lib/packet/ServerPacketCounter",
                "noticeServerboundPacket",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                false
            )
        }
    }
}
