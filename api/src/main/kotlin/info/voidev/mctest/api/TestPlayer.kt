package info.voidev.mctest.api

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

    /**
     * Waits until the client has received all packets that,
     * up until calling this method,
     * have been sent to it.
     *
     * **Implementation Note:**
     * The server-side player connection (the class `NetworkManager`)
     * is instrumented to increment a counter every time a packet is sent or enqueued.
     * This method yields ticks until that counter is equal to the number of packets
     * received by the client.
     */
    // TODO: Consider whether these methods should be present in the interface at all
    //  (maybe the test code doesn't need these specifics--they just add confusion?)
    suspend fun awaitClientboundPackets(scope: TickFunctionScope)

    suspend fun awaitServerboundPackets(scope: TickFunctionScope)

}
