package info.voidev.mctest.api.testplayer

import org.bukkit.GameMode
import java.util.UUID

interface ClientTabList {

    val entries: List<Entry>

    interface Entry {
        val playerId: UUID
        val name: String
        val displayNameText: String?
        val gameMode: GameMode?
        val ping: Int
    }
}
