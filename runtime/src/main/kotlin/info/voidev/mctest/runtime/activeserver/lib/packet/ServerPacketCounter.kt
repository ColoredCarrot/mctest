package info.voidev.mctest.runtime.activeserver.lib.packet

import info.voidev.mctest.runtime.activeserver.lib.nms.MinecraftInternals
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap
import org.bukkit.entity.Player

/**
 * Server-side packet counter.
 */
object ServerPacketCounter {

    @JvmStatic
    private val clientboundCounters = Reference2IntOpenHashMap<Any>()
    @JvmStatic
    private val serverboundCounters = Reference2IntOpenHashMap<Any>()

    @JvmStatic
    @Deprecated("Should not be called directly", level = DeprecationLevel.HIDDEN)
    @Suppress("unused")
    fun noticeClientboundPacket(nm: Any, packet: Any) {
        assertIsNetworkManager(nm)
        assertIsPacket(packet)

        synchronized(clientboundCounters) {
            clientboundCounters.addTo(nm, 1)
        }
    }

    @JvmStatic
    @Deprecated("Should not be called directly", level = DeprecationLevel.HIDDEN)
    @Suppress("unused")
    fun noticeServerboundPacket(nm: Any, packet: Any) {
        assertIsNetworkManager(nm)
        assertIsPacket(packet)

        synchronized(serverboundCounters) {
            serverboundCounters.addTo(nm, 1)
        }
    }

    @JvmStatic
    fun getClientboundPacketCount(player: Player): Int {
        val nm = MinecraftInternals.getNetworkManager(player)

        return synchronized(clientboundCounters) {
            clientboundCounters.getInt(nm)
        }
    }

    @JvmStatic
    fun getServerboundPacketCount(player: Player): Int {
        val nm = MinecraftInternals.getNetworkManager(player)

        return synchronized(serverboundCounters) {
            serverboundCounters.getInt(nm)
        }
    }

    private fun assertIsNetworkManager(nm: Any) {
        assert(nm.javaClass.name == MinecraftInternals.NetworkManager)
    }

    private fun assertIsPacket(packet: Any) {
        assert(Class.forName(MinecraftInternals.Packet).isInstance(packet))
    }
}
