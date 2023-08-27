package info.voidev.mctest.engine.server.platform.spigot

import info.voidev.mctest.engine.server.platform.MinecraftServerInstaller
import info.voidev.mctest.runtimesdk.versioning.minecraft.MinecraftVersion
import java.net.URI

class BuildToolsInstaller : MinecraftServerInstaller<MinecraftVersion>("BuildTools") {
    override fun install(version: MinecraftVersion): URI {
        TODO("Installing Spigot via BuildTools is not yet implemented")
    }
}
