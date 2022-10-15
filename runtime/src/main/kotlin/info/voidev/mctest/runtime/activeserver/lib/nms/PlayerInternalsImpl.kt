package info.voidev.mctest.runtime.activeserver.lib.nms

import org.bukkit.entity.Player
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

object PlayerInternalsImpl : PlayerInternals {

    @Volatile
    private var getHandleHandle: MethodHandle? = null

    private fun getHandleHandle(player: Player): MethodHandle {
        getHandleHandle?.also { return it }

        val handle = MethodHandles.publicLookup().findVirtual(
            player.javaClass,
            "getHandle",
            MethodType.methodType(MinecraftInternals.EntityPlayerCls)
        )

        getHandleHandle = handle
        return handle
    }

    override fun getEntityPlayer(player: Player): Any {
        return getHandleHandle(player).invoke(player)
    }
}
