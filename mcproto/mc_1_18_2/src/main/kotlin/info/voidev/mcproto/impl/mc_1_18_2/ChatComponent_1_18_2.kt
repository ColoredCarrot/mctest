package info.voidev.mcproto.impl.mc_1_18_2

import info.voidev.mcproto.api.ChatComponent
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.SelectorComponent
import net.md_5.bungee.api.chat.TextComponent
import net.kyori.adventure.text.SelectorComponent as ProtoSelectorComponent
import net.kyori.adventure.text.TextComponent as ProtoTextComponent

class ChatComponent_1_18_2(comp: Component) : ChatComponent() {

    private val bukkit by lazy { convertComponent(comp) }

    override fun get() = bukkit
}

private fun convertComponent(comp: Component): BaseComponent {
    val res = when (comp) {
        is ProtoTextComponent -> convertComponent(comp)
        is ProtoSelectorComponent -> convertComponent(comp)
        else -> TextComponent(comp.toString())
    }
    for (child in comp.children()) {
        res.addExtra(convertComponent(child))
    }
    return res
}

private fun convertComponent(comp: ProtoTextComponent) = TextComponent(comp.content())

private fun convertComponent(comp: ProtoSelectorComponent) = SelectorComponent(comp.pattern())
