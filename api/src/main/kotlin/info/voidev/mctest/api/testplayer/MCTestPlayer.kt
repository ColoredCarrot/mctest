package info.voidev.mctest.api.testplayer

import info.voidev.mctest.api.MCTest
import org.bukkit.permissions.PermissionAttachment

/**
 * Configuration options for [TestPlayer] parameters of `@`[MCTest] methods.
 *
 * ### Usage:
 * ```kotlin
 * suspend fun TestScope.`foo test`(
 *     @MCTestPlayer(name = "Foo")
 *     player: TestPlayer,
 * ) { ... }
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MCTestPlayer(
    /**
     * The name of the test player.
     * If empty, a name is generated from the parameter name.
     *
     * Note that if two test players have the same name,
     * their UUIDs will be equal as well.
     */
    val name: String = "",

    /**
     * Whether the player should have OP permissions.
     * More fine-grained permissions controls are available via [permissions]
     * or, failing that, setting up permissions yourself at the start of the test method.
     */
    val op: Boolean = true,

    /**
     * The player will be awarded these permissions upon joining.
     *
     * The corresponding [PermissionAttachment] will belong to the testee plugin.
     */
    val permissions: Array<String> = [],
)
