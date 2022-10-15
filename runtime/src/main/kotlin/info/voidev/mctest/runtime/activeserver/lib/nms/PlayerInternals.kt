package info.voidev.mctest.runtime.activeserver.lib.nms

import org.bukkit.entity.Player

interface PlayerInternals {

    fun getEntityPlayer(player: Player): Any

}
