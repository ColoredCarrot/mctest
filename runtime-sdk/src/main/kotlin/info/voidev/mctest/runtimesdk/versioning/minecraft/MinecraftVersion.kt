package info.voidev.mctest.runtimesdk.versioning.minecraft

import info.voidev.mctest.runtimesdk.versioning.MalformedVersionException
import info.voidev.mctest.runtimesdk.versioning.Version

data class MinecraftVersion(
    val major: UInt,
    val minor: UInt,
) : Version<MinecraftVersion>() {

    override fun toString() = toString(major, minor)

    override fun compareTo(other: MinecraftVersion): Int {
        return if (major != other.major) major.compareTo(other.major)
        else minor.compareTo(other.minor)
    }

    companion object {
        operator fun invoke(version: String): MinecraftVersion {
            val string = version.replace("\\s+".toRegex(), "")

            if (!string.startsWith("1.")) {
                throw MalformedVersionException(string, "Should start with 1.")
            }

            val withoutPrefix = string.substring("1.".length)
            val split = withoutPrefix.split('.', limit = 2)

            val major = parseComponent(split[0], string, "Major")
            val minor = if (split.size > 1) parseComponent(split[1], string, "Minor") else 0u

            return MinecraftVersion(major, minor)
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
