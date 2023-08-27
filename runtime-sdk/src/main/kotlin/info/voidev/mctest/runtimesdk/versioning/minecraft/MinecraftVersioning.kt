package info.voidev.mctest.runtimesdk.versioning.minecraft

import info.voidev.mctest.runtimesdk.versioning.Versioning

object MinecraftVersioning : Versioning<MinecraftVersion>(
    MinecraftVersion(version = "1.8")..MinecraftVersion("1.18.2"),
) {

    override fun resolve(versionString: String) = MinecraftVersion(versionString)

}
