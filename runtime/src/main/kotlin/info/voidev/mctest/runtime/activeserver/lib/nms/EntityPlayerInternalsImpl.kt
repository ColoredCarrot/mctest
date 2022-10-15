package info.voidev.mctest.runtime.activeserver.lib.nms

import info.voidev.mctest.runtime.activeserver.lib.nms.MinecraftInternals.EntityPlayer
import info.voidev.mctest.runtime.activeserver.lib.nms.MinecraftInternals.NetworkManager
import info.voidev.mctest.runtime.activeserver.lib.nms.MinecraftInternals.PlayerConnection
import java.lang.reflect.Field

object EntityPlayerInternalsImpl : EntityPlayerInternals {

    private val playerConnectionField: Field = Class
        .forName(EntityPlayer)
        .getField("b")

    private val networkManagerField: Field = Class
        .forName(PlayerConnection)
        .getField("a")

    init {
        require(playerConnectionField.type.name == PlayerConnection)
        require(networkManagerField.type.name == NetworkManager)
    }

    override fun getNetworkManager(entityPlayer: Any): Any {
        val playerConn = playerConnectionField.get(entityPlayer)
        return networkManagerField.get(playerConn)
    }

}
