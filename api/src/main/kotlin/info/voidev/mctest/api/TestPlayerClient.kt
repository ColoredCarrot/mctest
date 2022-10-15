package info.voidev.mctest.api

import info.voidev.mctest.api.testplayer.ClientTabList
import org.bukkit.entity.Entity

interface TestPlayerClient {

    /**
     * Sends a Chat packet to the server
     * and waits for (suspends until) an acknowledgement.
     *
     * The server sends the acknowledgement as soon as
     * the action the server associates with the chat message
     * has been performed.
     * Usually, this is one server tick after the message has been processed.
     */
    fun say(message: String)

    val receivedMessages: List<String>

    val knownEntities: List<Entity>

    val tabList: ClientTabList

    // TODO: We might make MCProtocolLib an api dependency as well,
    //  so that we could have received/sentPackets and corresponding AssertJ extensions

}
