package info.voidev.mctest.plugin

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MctestServerRuntimePlugin : JavaPlugin() {

    override fun onLoad() {
        Bukkit.getPluginManager().registerEvents(MctestServerRuntimeOptimizer, this)
    }

    override fun onEnable() {
    }


}
