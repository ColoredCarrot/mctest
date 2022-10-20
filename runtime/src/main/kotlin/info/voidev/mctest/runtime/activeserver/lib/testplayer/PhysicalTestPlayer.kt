package info.voidev.mctest.runtime.activeserver.lib.testplayer

import info.voidev.mctest.api.testplayer.TestPlayer
import info.voidev.mctest.api.TickFunctionScope
import info.voidev.mctest.api.testplayer.TestPlayerDisconnectedException
import info.voidev.mctest.api.yieldTicksUntil
import info.voidev.mctest.runtime.activeserver.lib.packet.ServerPacketCounter
import org.bukkit.entity.Player

class PhysicalTestPlayer(
    override val server: Player,
    override val client: PhysicalTestPlayerClient,
) : TestPlayer, Player by server {

    suspend fun syncOwnPackets(scope: TickFunctionScope) {
        scope.yieldTicksUntil {
            if (!server.isOnline) throw TestPlayerDisconnectedException(this)

            client.receivedPacketsCount >= ServerPacketCounter.getClientboundPacketCount(server) &&
                    ServerPacketCounter.getServerboundPacketCount(server) >= client.sentPacketsCount
        }
    }

}
