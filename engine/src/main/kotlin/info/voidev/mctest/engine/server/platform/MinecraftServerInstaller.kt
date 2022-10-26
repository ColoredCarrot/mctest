package info.voidev.mctest.engine.server.platform

import java.net.URI

abstract class MinecraftServerInstaller<in V : MinecraftPlatform.Version<*>>(
    /**
     * The human-readable name of this installer,
     * unique among the installers of a specific platform.
     */
    val name: String,
) {

    abstract fun install(version: V): URI

    override fun toString() = name
}
