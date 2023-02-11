package info.voidev.mctest.runtime.activeserver

import info.voidev.mcproto.api.MinecraftFacade
import info.voidev.mctest.runtime.activeserver.lib.nms.MinecraftVersionDiscovery

/**
 * Global proxy to the version-specific [MinecraftFacade] implementation.
 *
 * Can be static because, for a specific server version, this class is loaded in a different JVM;
 * therefore, each class corresponds to exactly one server version.
 */
object MinecraftVersionFacade {

    val minecraftFacade by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val mcVersion = MinecraftVersionDiscovery.discoverVersion().replace('.', '_')
        val minecraftFacadeCls = Class.forName(PACKAGE_TEMPLATE.replace("%", mcVersion))
        minecraftFacadeCls.getConstructor().newInstance() as MinecraftFacade
    }

    private const val PACKAGE_TEMPLATE = "info.voidev.mcproto.impl.mc_%.MinecraftFacade_%"
}
