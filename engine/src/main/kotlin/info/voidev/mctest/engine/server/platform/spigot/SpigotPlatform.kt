package info.voidev.mctest.engine.server.platform.spigot

import info.voidev.mctest.engine.server.platform.MinecraftPlatform
import info.voidev.mctest.runtimesdk.versioning.minecraft.MinecraftVersion
import info.voidev.mctest.runtimesdk.versioning.minecraft.MinecraftVersioning

object SpigotPlatform : MinecraftPlatform<MinecraftVersion>("Spigot", "spigot", MinecraftVersioning) {

    override val defaultVersion get() = resolveVersion("1.18.2")

    override val availableInstallers =
        listOf(
            GetBukkitInstaller(),
            BuildToolsInstaller(),
        )

}
