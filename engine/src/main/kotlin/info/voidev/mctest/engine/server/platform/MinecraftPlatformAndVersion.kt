package info.voidev.mctest.engine.server.platform

import info.voidev.mctest.runtimesdk.versioning.Version

class MinecraftPlatformAndVersion<V : Version<V>>(
    val platform: MinecraftPlatform<V>,
    val version: V,
) {
    override fun toString(): String {
        return "$platform $version"
    }

    companion object {
        operator fun invoke(s: String): MinecraftPlatformAndVersion<*> {
            // Version cannot contain whitespace, so we can safely split around the last space
            val lastSpace = s.lastIndexOf(' ')
            val platform = MinecraftPlatforms.byName(s.substring(0, lastSpace))
                ?: throw IllegalArgumentException("Malformed Minecraft platform-version combination: $s")
            return create(platform, s.substring(lastSpace))
        }

        private fun <V : Version<V>> create(platform: MinecraftPlatform<V>, versionString: String) =
            MinecraftPlatformAndVersion(platform, platform.resolveVersion(versionString))
    }
}
