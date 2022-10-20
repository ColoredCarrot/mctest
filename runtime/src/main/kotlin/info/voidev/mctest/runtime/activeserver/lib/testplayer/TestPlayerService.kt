package info.voidev.mctest.runtime.activeserver.lib.testplayer

import info.voidev.mctest.api.TickFunctionScope
import info.voidev.mctest.api.yieldTicksUntilNotNull
import info.voidev.mctest.runtime.activeserver.testeePluginInstance
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object TestPlayerService {

    suspend fun join(spec: TestPlayerSpec, tickScope: TickFunctionScope): PhysicalTestPlayer {
        require(Bukkit.isPrimaryThread())

        val client = PhysicalTestPlayerClient(spec, Bukkit.getPort(), tickScope)

        // Connect on a separate thread so as not to block the primary thread
        // (we will be yielding ticks instead)
        Bukkit.getScheduler().runTaskAsynchronously(testeePluginInstance, Runnable {
            client.connect()
        })

        //TODO make timeout configurable
        val player = tickScope.yieldTicksUntilNotNull(20 * 10) { Bukkit.getPlayerExact(spec.name) }

        player.isOp = spec.op
        setUpPermissions(player, spec.permissions)

        return PhysicalTestPlayer(player, client)
    }

    private fun setUpPermissions(player: Player, permissions: List<String>) {
        val attachment = player.addAttachment(testeePluginInstance)
        for (permission in permissions) {
            attachment.setPermission(permission, true)
        }
    }
}
