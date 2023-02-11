package info.voidev.mcproto.api

import org.bukkit.GameMode
import java.util.UUID

interface TabList : Iterable<TabList.Entry> {

    data class Entry(
        val playerId: UUID,
        val name: String,
        val displayName: ChatComponent?,
        val gameMode: GameMode?,
        val ping: Int,
    )
}
