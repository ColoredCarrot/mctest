package info.voidev.mctest.runtime.activeserver.lib.testplayer.state

import com.github.steveice10.mc.protocol.data.game.PlayerListEntry
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundPlayerInfoPacket
import info.voidev.mctest.api.Assertions
import info.voidev.mctest.api.testplayer.ClientTabList
import info.voidev.mctest.runtime.activeserver.lib.chatcomponent.extractText
import net.kyori.adventure.text.Component
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.GameMode as BukkitGameMode

class ClientTabListImpl : ClientTabList {

    private val entriesMap = ConcurrentHashMap<UUID, Entry>()

    override val entries: List<ClientTabList.Entry>
        get() = entriesMap.values.toList()

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
            if (entry != null) {
                entry.gameMode = mapGameMode(update.gameMode)
                entry
            } else {
                createEntry(update)
            }
        }
    }

    private fun updateLatency(update: PlayerListEntry) {
        entriesMap.compute(update.profile.id) { _, entry ->
            if (entry != null) {
                entry.ping = update.ping
                entry
            } else {
                createEntry(update)
            }
        }
    }

    private fun updateDisplayName(update: PlayerListEntry) {
        entriesMap.compute(update.profile.id) { _, entry ->
            if (entry != null) {
                entry.displayName = update.displayName
                entry
            } else {
                createEntry(update)
            }
        }
    }

    private fun removePlayer(update: PlayerListEntry) {
        val removed = entriesMap.remove(update.profile.id)
        if (removed == null) {
            System.err.println("Got player removal PlayerInfo packet for unknown player: $update")
        }
    }

    private fun createEntry(update: PlayerListEntry) = Entry(
        playerId = update.profile.id,
        name = update.profile.name ?: "<unnamed>",
        displayName = update.displayName,
        gameMode = mapGameMode(update.gameMode),
        ping = update.ping,
    )

    private fun mapGameMode(gameMode: GameMode?): BukkitGameMode? = when (gameMode) {
        GameMode.SURVIVAL -> BukkitGameMode.SURVIVAL
        GameMode.CREATIVE -> BukkitGameMode.CREATIVE
        GameMode.ADVENTURE -> BukkitGameMode.ADVENTURE
        GameMode.SPECTATOR -> BukkitGameMode.SPECTATOR
        GameMode.UNKNOWN, null -> null
    }

    data class Entry(
        override val playerId: UUID,
        override val name: String,
        var displayName: Component?,
        override var gameMode: BukkitGameMode?,
        override var ping: Int,
    ) : ClientTabList.Entry {
        override val displayNameText: String?
            get() = displayName?.extractText()
    }
}
