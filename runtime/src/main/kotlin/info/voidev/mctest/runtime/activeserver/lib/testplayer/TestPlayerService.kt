package info.voidev.mctest.runtime.activeserver.lib.testplayer

import info.voidev.mctest.api.TickFunctionScope
import info.voidev.mctest.api.yieldTicksUntilNotNull
import info.voidev.mctest.runtime.activeserver.testeePluginInstance
import org.bukkit.Bukkit

object TestPlayerService {

    suspend fun join(spec: TestPlayerSpec, tickScope: TickFunctionScope): PhysicalTestPlayer {
        require(Bukkit.isPrimaryThread())

        val client = TestPlayerClientImpl(spec, Bukkit.getPort(), tickScope)
        // Connect on a separate thread
        Bukkit.getScheduler().runTaskAsynchronously(testeePluginInstance, Runnable {
            client.connect()
        })

        //TODO reasonable timeout
        val player = tickScope.yieldTicksUntilNotNull(20 * 10) { Bukkit.getPlayerExact(spec.name) }

        return PhysicalTestPlayer(player, client)
    }
}
