package info.voidev.mctest.engine.server.platform.spigot

import info.voidev.mctest.engine.server.platform.MinecraftServerInstaller
import info.voidev.mctest.runtimesdk.versioning.minecraft.MinecraftVersion
import java.net.URI

class GetBukkitInstaller : MinecraftServerInstaller<MinecraftVersion>("GetBukkit") {
    override fun install(version: MinecraftVersion): URI {
        return URI("https://download.getbukkit.org/spigot/spigot-$version.jar")
    }
}
