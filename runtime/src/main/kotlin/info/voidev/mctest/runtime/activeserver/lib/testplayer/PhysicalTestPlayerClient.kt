package info.voidev.mctest.runtime.activeserver.lib.testplayer

import com.github.steveice10.mc.auth.data.GameProfile
import com.github.steveice10.mc.protocol.MinecraftConstants
import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket
import com.github.steveice10.packetlib.Session
import com.github.steveice10.packetlib.event.session.DisconnectingEvent
import com.github.steveice10.packetlib.event.session.PacketSendingEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpClientSession
import info.voidev.mctest.api.TestScope
import info.voidev.mctest.api.testplayer.TestPlayerClient
import info.voidev.mctest.runtime.activeserver.lib.chatcomponent.extractText
import info.voidev.mctest.runtime.activeserver.lib.testplayer.state.ClientState
import org.bukkit.entity.Entity
import java.util.Collections
import java.util.UUID

class PhysicalTestPlayerClient(spec: TestPlayerSpec, port: Int, private val scope: TestScope) : TestPlayerClient {

    companion object {
        private const val NORMAL_DISCONNECT_REASON = "Test has completed"
    }

    private val session: TcpClientSession

    private val sentPackets = Collections.synchronizedList(ArrayList<Packet>())
    private val recvPackets = Collections.synchronizedList(ArrayList<Packet>())

    private val state = ClientState()

    init {
        val mcProtocol = MinecraftProtocol(GameProfile(null as UUID?, spec.name), null)
        session = TcpClientSession("localhost", port, mcProtocol)
        session.addListener(object : SessionAdapter() {
            override fun packetSending(event: PacketSendingEvent) {
                // We can't use packetSent since that is only called somewhere down the line,
                //  but we need to update the counter right away.
                // If sending fails, something has gone terribly wrong anyway,
                //  so keeping the counter in sync in that case is not important.
                sentPackets += event.getPacket<Packet>()
            }

            override fun packetReceived(session: Session, packet: Packet) {
                recvPackets += packet
            }

            override fun disconnecting(event: DisconnectingEvent) {
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
        session.addListener(state)
    }

    fun connect() {
        session.connect()
    }

    fun disconnect() {
        session.disconnect(NORMAL_DISCONNECT_REASON)
    }

    override suspend fun say(message: String) {
        session.send(ServerboundChatPacket(message))
        scope.syncPackets()
    }

    val receivedPackets get() = recvPackets.toList()

    val receivedPacketsCount get() = recvPackets.size

    val sentPacketsCount get() = sentPackets.size

    override val receivedMessages: List<String>
        get() = receivedPackets.mapNotNull {
            (it as? ClientboundChatPacket)?.message?.extractText()
        }

    override val knownEntities: List<Entity>
        get() = TODO()

    override val tabList get() = state.tabList
}
