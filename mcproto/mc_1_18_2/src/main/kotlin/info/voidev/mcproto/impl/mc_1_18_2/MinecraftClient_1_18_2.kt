package info.voidev.mcproto.impl.mc_1_18_2

import com.github.steveice10.mc.auth.data.GameProfile
import com.github.steveice10.mc.protocol.MinecraftConstants
import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundPlayerInfoPacket
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket
import com.github.steveice10.packetlib.Session
import com.github.steveice10.packetlib.event.session.DisconnectingEvent
import com.github.steveice10.packetlib.event.session.PacketSendingEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpClientSession
import info.voidev.mcproto.api.ChatMessage
import info.voidev.mcproto.api.MinecraftClient
import java.util.UUID

class MinecraftClient_1_18_2(host: String, port: Int, playerName: String) : MinecraftClient {

    private val sessionLock: Any = this

    private val session: TcpClientSession

    override fun connect() {
        session.connect(true)
    }

    override fun disconnect() {
        session.disconnect(NORMAL_DISCONNECT_REASON)
    }

    override var numSentPackets = 0
        private set
    override var numReceivedPackets = 0
        private set

    override val tabList = TabList_1_18_2()
    override val receivedMessages = ArrayList<ChatMessage>()

    init {
        val mcProtocol = MinecraftProtocol(GameProfile(null as UUID?, playerName), null)
        session = TcpClientSession(host, port, mcProtocol)
        session.addListener(object : SessionAdapter() {
            override fun packetSending(event: PacketSendingEvent): Unit = synchronized(sessionLock) {
                // We can't use packetSent since that is only called somewhere down the line,
                //  but we need to update the counter right away.
                // If sending fails, something has gone terribly wrong anyway,
                //  so keeping the counter in sync in that case is not important.
                ++numSentPackets
            }

            override fun packetReceived(session: Session, packet: Packet) = synchronized(sessionLock) {
                ++numReceivedPackets

                when (packet) {
                    is ClientboundPlayerInfoPacket -> tabList.update(packet)
                    is ClientboundChatPacket -> receivedMessages += ChatMessage(
                        ChatComponent_1_18_2(packet.message),
                        packet.senderUuid
                    )
                    // else, silently drop packet--we're not listening for it
                }
            }

            override fun disconnecting(event: DisconnectingEvent) = synchronized(sessionLock) {
                if (event.cause != null || event.reason != NORMAL_DISCONNECT_REASON) {
                    // This method can called when we (the client) decide to disconnect.
                    // In addition to the normal case (test complete or TODO the test intentionally kicks the player),
                    // this can happen, for example, in case an unexpected exception is thrown while handling packetReceived().
                    // TODO: Report this as an internal error/bug via RMI to the test engine, which will then terminate
                    val profile: GameProfile? = event.session.getFlag(MinecraftConstants.PROFILE_KEY)
                    System.err.println("INTERNAL EXCEPTION. PLEASE REPORT THIS.  Reason: ${event.reason}  Cause: ${event.cause}  Profile: $profile")
                    event.cause?.printStackTrace()
                }
            }
        })
    }

    override fun say(message: String) = synchronized(sessionLock) {
        session.send(ServerboundChatPacket(message))
    }

    companion object {
        private const val NORMAL_DISCONNECT_REASON = "NORMAL_DISCONNECT_REASON"
    }
}
