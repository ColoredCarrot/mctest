package info.voidev.mctest.engine.server.platform.spigot

import info.voidev.mctest.engine.server.platform.MalformedVersionException
import info.voidev.mctest.engine.server.platform.MinecraftPlatform

class SpigotPlatform : MinecraftPlatform<SpigotPlatform.Version> {

    override fun resolveVersion(version: String) = Version.parse(version)

    override val availableInstallers =
        listOf(
            GetBukkitInstaller(),
            BuildToolsInstaller(),
        )

    data class Version(
        val major: UInt,
        val minor: UInt,
    ) : MinecraftPlatform.Version<Version>("spigot-${toString(major, minor)}") {

        override fun toString() = Companion.toString(major, minor)

        override fun compareTo(other: Version): Int {
            return if (major != other.major) major.compareTo(other.major)
            else minor.compareTo(other.minor)
        }

        companion object {
            fun parse(version: String): Version {
                if (!version.startsWith("1.")) {
                    throw MalformedVersionException(version, "Should start with 1.")
                }

                val withoutPrefix = version.substring("1.".length)
                val split = withoutPrefix.split('.', limit = 2)

                val major = parseComponent(split[0], version, "Major")
                val minor = if (split.size > 1) parseComponent(split[1], version, "Minor") else 0u

                return Version(major, minor)
            }

            private fun parseComponent(comp: String, version: String, compName: String): UInt {
                return comp.toUIntOrNull()
                    ?: throw MalformedVersionException(version, "$compName version should be an unsigned integer")
            }

            private fun toString(major: UInt, minor: UInt) =
                if (minor == 0u) "1.$major"
                else "1.$major.$minor"
        }
    }
}
