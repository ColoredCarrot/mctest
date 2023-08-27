package info.voidev.mctest.engine.server.platform

import info.voidev.mctest.runtimesdk.versioning.MalformedVersionException
import info.voidev.mctest.runtimesdk.versioning.Version
import info.voidev.mctest.runtimesdk.versioning.Versioning

/**
 * A Minecraft server platform, like Spigot or Paper.
 */
abstract class MinecraftPlatform<V : Version<V>>(
    /**
     * The human-readable platform name, like "Spigot" or "Paper".
     */
    val name: String,
    val filenamePrefix: String,
    val versioning: Versioning<V>,
) {

    /**
     * Parses a [version string][version] to a version.
     */
    @Throws(MalformedVersionException::class)
    open fun resolveVersion(version: String): V = versioning.resolve(version)

    /**
     * If MCTest is unable to infer an appropriate version,
     * it uses the platform's default.
     */
    abstract val defaultVersion: V

    /**
     * List of available installers for the server JAR.
     *
     * An installer's index in this list indicates its preference:
     * Installers with a smaller index are preferred to installers with a higher index.
     */
    abstract val availableInstallers: List<MinecraftServerInstaller<V>>

    open fun resolveInstaller(name: String) = availableInstallers.firstOrNull { it.name == name }

    override fun toString() = name
}
