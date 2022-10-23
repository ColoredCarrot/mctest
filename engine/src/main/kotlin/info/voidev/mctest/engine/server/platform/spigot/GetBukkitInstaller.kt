package info.voidev.mctest.engine.server.platform.spigot

import info.voidev.mctest.engine.server.platform.MinecraftServerInstaller
import java.net.URI

class GetBukkitInstaller : MinecraftServerInstaller<SpigotPlatform.Version>("GetBukkit") {
    override fun install(version: SpigotPlatform.Version): URI {
        return URI("https://download.getbukkit.org/spigot/spigot-$version.jar")
    }
}
