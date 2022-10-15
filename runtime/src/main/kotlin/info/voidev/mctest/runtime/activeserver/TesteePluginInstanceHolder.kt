package info.voidev.mctest.runtime.activeserver

import org.bukkit.Bukkit

val testeePluginInstance get() = Bukkit.getPluginManager().getPlugin(System.getProperty("mctest.testee.plugin.name"))!!
