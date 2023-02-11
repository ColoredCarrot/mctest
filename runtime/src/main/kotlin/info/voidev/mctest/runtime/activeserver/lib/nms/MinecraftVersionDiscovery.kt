package info.voidev.mctest.runtime.activeserver.lib.nms

import org.bukkit.Bukkit

object MinecraftVersionDiscovery {

    fun discoverVersion(): String {
        // Discover version in the form 1.18.2
        return bukkitVersionPattern.matchEntire(Bukkit.getBukkitVersion())!!.groupValues[1]
    }

    private val bukkitVersionPattern = Regex("""(\d\.\d\d?(\.\d\d?)?)-R.+""")
}
