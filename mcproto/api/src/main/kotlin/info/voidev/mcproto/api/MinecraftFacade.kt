package info.voidev.mcproto.api

/**
 * Main entry-point to Minecraft version-specific facilities.
 *
 * Implementations must have a public, zero-arguments constructor.
 */
interface MinecraftFacade {
    val clientFactory: MinecraftClientFactory
}
