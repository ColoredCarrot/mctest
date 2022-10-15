package info.voidev.mctest.runtime.activeserver.lib.tickable

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

object TickableDispatcher {
    fun install(plugin: Plugin, tickable: Tickable) {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            tickable.tick()
        }, 1L, 1L)
    }
}
