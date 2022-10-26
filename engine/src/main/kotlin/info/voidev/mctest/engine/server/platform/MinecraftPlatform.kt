package info.voidev.mctest.engine.server.platform

/**
 * A Minecraft server platform, like Spigot or Paper.
 */
abstract class MinecraftPlatform<V : MinecraftPlatform.Version<V>>(
    /**
     * The human-readable platform name, like "Spigot" or "Paper".
     */
    val name: String,
) {

    /**
     * Parses a [version string][version] to a version.
     */
    @Throws(MalformedVersionException::class)
    abstract fun resolveVersion(version: String): V

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

    abstract class Version<V : Version<V>>(filenameWithoutExt: String) : Comparable<V> {

        val filename = "$filenameWithoutExt.jar"

        init {
            require(filenameWithoutExt.none { it.isWhitespace() })
        }

        abstract override fun toString(): String
    }
}
