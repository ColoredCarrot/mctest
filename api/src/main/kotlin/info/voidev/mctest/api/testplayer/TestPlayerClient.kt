package info.voidev.mctest.api.testplayer

import info.voidev.mcproto.api.ChatMessage
import info.voidev.mcproto.api.MinecraftClient
import info.voidev.mcproto.api.TabList

/**
 * Basically a [MinecraftClient], but optimized for usage in tests.
 * Methods that send packets to the server suspend until those packets were received.
 */
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
    suspend fun say(message: String)

    val receivedMessages: List<ChatMessage>

    val tabList: TabList

}
