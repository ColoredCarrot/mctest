package info.voidev.mctest.engine.server.platform

import info.voidev.mctest.engine.server.platform.spigot.SpigotPlatform

object MinecraftPlatforms {

    val all = setOf(SpigotPlatform)

    fun byName(name: String): MinecraftPlatform<*>? =
        all.firstOrNull { it.name == name }
}
