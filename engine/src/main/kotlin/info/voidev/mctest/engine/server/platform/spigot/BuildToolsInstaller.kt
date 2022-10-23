package info.voidev.mctest.engine.server.platform.spigot

import info.voidev.mctest.engine.server.platform.MinecraftServerInstaller
import java.net.URI

class BuildToolsInstaller : MinecraftServerInstaller<SpigotPlatform.Version>("BuildTools") {
    override fun install(version: SpigotPlatform.Version): URI {
        TODO("Installing Spigot via BuildTools is not yet implemented")
    }
}
