package info.voidev.mctest.engine.server.platform

import info.voidev.mctest.runtimesdk.versioning.Version
import java.net.URI

abstract class MinecraftServerInstaller<in V : Version<*>>(
    /**
     * The human-readable name of this installer,
     * unique among the installers of a specific platform.
     */
    val name: String,
) {

    abstract fun install(version: V): URI

    override fun toString() = name
}
