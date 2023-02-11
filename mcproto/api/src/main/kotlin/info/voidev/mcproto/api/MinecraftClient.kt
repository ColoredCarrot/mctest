package info.voidev.mcproto.api

interface MinecraftClient {

    fun connect()
    fun disconnect()

    val numSentPackets: Int
    val numReceivedPackets: Int

    val tabList: TabList

    val receivedMessages: List<ChatMessage>

    fun say(message: String)

}
