package info.voidev.mctest.runtime.activeserver.lib.testplayer.state

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundPlayerInfoPacket
import com.github.steveice10.packetlib.Session
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet

/**
 * Tracks a Minecraft client's state by inspecting its incoming packets.
 */
class ClientState : SessionAdapter() {

    val tabList = ClientTabListImpl()

    override fun packetReceived(session: Session, packet: Packet) {
        when (packet) {
            is ClientboundPlayerInfoPacket -> tabList.update(packet)
            else -> {
                // Nothing to do
            }
        }
    }

}
