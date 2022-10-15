package info.voidev.mctest.runtime.activeserver.lib.chatcomponent

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.md_5.bungee.chat.ComponentSerializer

fun Component.extractText(): String {
    val jsonString = GsonComponentSerializer.gson().serialize(this)
    return ComponentSerializer.parse(jsonString).joinToString(separator = "") { it.toPlainText() }
}
