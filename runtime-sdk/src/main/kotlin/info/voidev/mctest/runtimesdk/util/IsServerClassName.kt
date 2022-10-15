package info.voidev.mctest.runtimesdk.util

import java.util.function.Predicate

/**
 * A test for whether a class is contained in the server JAR.
 * Includes libraries.
 */
object IsServerClassName : Predicate<String> {
    override fun test(t: String) = PREFIXES.any(t::startsWith)

    private val PREFIXES = listOf(
        "net.minecraft.",
        "com.mojang.",
        "org.bukkit.",
        "org.spigotmc.",
        "io.netty.",
        "joptsimple.",
        "org.fusesource.",
        "org.slf4j.",
        "org.apache.logging.",
        "com.google.common.",
        "com.google.gson.",
        "jline.",
        "org.yaml.",
        "org.eclipse.aether.",
    )
}
