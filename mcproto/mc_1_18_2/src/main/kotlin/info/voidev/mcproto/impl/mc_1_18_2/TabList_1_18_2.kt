package info.voidev.mcproto.impl.mc_1_18_2

import com.github.steveice10.mc.protocol.data.game.PlayerListEntry
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundPlayerInfoPacket
import info.voidev.mcproto.api.TabList
import org.bukkit.GameMode
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode as ProtoGameMode

class TabList_1_18_2 : TabList {

    private val entriesMap = ConcurrentHashMap<UUID, TabList.Entry>()

    override fun iterator() = entriesMap.values.iterator()

    fun update(packet: ClientboundPlayerInfoPacket) {
        when (packet.action) {
            PlayerListEntryAction.ADD_PLAYER -> packet.entries.forEach(this::addPlayer)
            PlayerListEntryAction.UPDATE_GAMEMODE -> packet.entries.forEach(this::updateGameMode)
            PlayerListEntryAction.UPDATE_LATENCY -> packet.entries.forEach(this::updateLatency)
            PlayerListEntryAction.UPDATE_DISPLAY_NAME -> packet.entries.forEach(this::updateDisplayName)
            PlayerListEntryAction.REMOVE_PLAYER -> packet.entries.forEach(this::removePlayer)
        }
    }

    private fun addPlayer(update: PlayerListEntry) {
        // Servers may send multiple "add player" PlayerInfo packets for the same player,
        //  see https://bukkit.org/threads/protocollib-onpacketsending-being-called-twice.131998/
        // We just replace any old entries
        entriesMap[update.profile.id] = createEntry(update)
    }

    private fun updateGameMode(update: PlayerListEntry) {
        entriesMap.compute(update.profile.id) { _, entry ->
            entry?.copy(gameMode = mapGameMode(update.gameMode))
                ?: createEntry(update)
        }
    }

    private fun updateLatency(update: PlayerListEntry) {
        entriesMap.compute(update.profile.id) { _, entry ->
            entry?.copy(ping = update.ping)
                ?: createEntry(update)
        }
    }

    private fun updateDisplayName(update: PlayerListEntry) {
        entriesMap.compute(update.profile.id) { _, entry ->
            entry?.copy(displayName = update.displayName?.let(::ChatComponent_1_18_2))
                ?: createEntry(update)
        }
    }

    private fun removePlayer(update: PlayerListEntry) {
        val removed = entriesMap.remove(update.profile.id)
        if (removed == null) {
            System.err.println("Got player removal PlayerInfo packet for unknown player: $update")
        }
    }

    private fun createEntry(update: PlayerListEntry) = TabList.Entry(
        playerId = update.profile.id,
        name = update.profile.name ?: "<unnamed>",
        displayName = update.displayName?.let(::ChatComponent_1_18_2),
        gameMode = mapGameMode(update.gameMode),
        ping = update.ping,
    )

    private fun mapGameMode(gameMode: ProtoGameMode?): GameMode? = when (gameMode) {
        ProtoGameMode.SURVIVAL -> GameMode.SURVIVAL
        ProtoGameMode.CREATIVE -> GameMode.CREATIVE
        ProtoGameMode.ADVENTURE -> GameMode.ADVENTURE
        ProtoGameMode.SPECTATOR -> GameMode.SPECTATOR
        ProtoGameMode.UNKNOWN, null -> null
    }
}
