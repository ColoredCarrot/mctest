package info.voidev.mctest.runtime.activeserver.lib.testplayer

import info.voidev.mctest.api.TestPlayer
import info.voidev.mctest.api.TickFunctionScope
import info.voidev.mctest.api.testplayer.TestPlayerDisconnectedException
import info.voidev.mctest.api.yieldTicksUntil
import info.voidev.mctest.runtime.activeserver.lib.packet.ServerPacketCounter
import info.voidev.mctest.runtime.activeserver.lib.perf.Performance
import org.bukkit.entity.Player

class PhysicalTestPlayer(
    override val server: Player,
    override val client: TestPlayerClientImpl,
) : TestPlayer, Player by server {

    override suspend fun awaitClientboundPackets(scope: TickFunctionScope) =
        Performance.section("awaitClientboundPackets") {
            scope.yieldTicksUntil {
                if (!server.isOnline) throw TestPlayerDisconnectedException(this)

                ServerPacketCounter.getClientboundPacketCount(server) <= client.receivedPacketsCount
            }
        }

    override suspend fun awaitServerboundPackets(scope: TickFunctionScope) =
        Performance.section("awaitServerboundPackets") {
            scope.yieldTicksUntil {
                if (!server.isOnline) throw TestPlayerDisconnectedException(this)

                ServerPacketCounter.getServerboundPacketCount(server) >= client.sentPacketsCount
            }
        }

    suspend fun syncOwnPackets(scope: TickFunctionScope) {
        scope.yieldTicksUntil {
            if (!server.isOnline) throw TestPlayerDisconnectedException(this)

            client.receivedPacketsCount >= ServerPacketCounter.getClientboundPacketCount(server) &&
                    ServerPacketCounter.getServerboundPacketCount(server) >= client.sentPacketsCount
        }
    }

}
