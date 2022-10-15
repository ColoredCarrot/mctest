package info.voidev.mctest.runtime.activeserver.lib.nms

import org.bukkit.entity.Player

object MinecraftInternals {
    const val EntityPlayer = "net.minecraft.server.level.EntityPlayer"
    const val PlayerConnection = "net.minecraft.server.network.PlayerConnection"
    const val NetworkManager = "net.minecraft.network.NetworkManager"
    const val Packet = "net.minecraft.network.protocol.Packet"

    val EntityPlayerCls: Class<*> = Class.forName(EntityPlayer)

    fun getNetworkManager(player: Player) =
        EntityPlayerInternalsImpl.getNetworkManager(PlayerInternalsImpl.getEntityPlayer(player))
}
