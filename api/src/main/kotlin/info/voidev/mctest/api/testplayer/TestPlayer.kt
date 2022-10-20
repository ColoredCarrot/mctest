package info.voidev.mctest.api.testplayer

import org.bukkit.entity.Player

/**
 * A player with a [client] that allows you to perform actions on behalf of that player.
 */
interface TestPlayer : Player {

    val client: TestPlayerClient

    /**
     * Provides access to the original player instance
     * in case you want to cast it to CraftPlayer.
     */
    val server: Player

}
