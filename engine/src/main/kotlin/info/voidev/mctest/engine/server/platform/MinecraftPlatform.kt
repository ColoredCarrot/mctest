package info.voidev.mctest.engine.server.platform

/**
 * A Minecraft server platform, like Spigot or Paper.
 */
interface MinecraftPlatform<V : MinecraftPlatform.Version<V>> {

    /**
     * Parses a [version string][version] to a version.
     */
    @Throws(MalformedVersionException::class)
    fun resolveVersion(version: String): V

    /**
     * List of available installers for the server JAR.
     *
     * An installer's index in this list indicates its preference:
     * Installers with a smaller index are preferred to installers with a higher index.
     */
    val availableInstallers: List<MinecraftServerInstaller<V>>

    fun resolveInstaller(name: String) = availableInstallers.firstOrNull { it.name == name }

    abstract class Version<V : Version<V>>(filenameWithoutExt: String) : Comparable<V> {

        val filename = "$filenameWithoutExt.jar"

        init {
            require(filenameWithoutExt.none { it.isWhitespace() })
        }

        abstract override fun toString(): String
    }
}
