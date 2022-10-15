package info.voidev.mctest.plugin

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldInitEvent

object MctestServerRuntimeOptimizer : Listener {

    @EventHandler
    fun onWorldInit(e: WorldInitEvent) {
        e.world.keepSpawnInMemory = false
        e.world.isAutoSave = false
    }

}
