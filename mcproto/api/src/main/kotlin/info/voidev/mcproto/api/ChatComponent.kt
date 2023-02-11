package info.voidev.mcproto.api

import net.md_5.bungee.api.chat.BaseComponent

abstract class ChatComponent {

    abstract fun get(): BaseComponent

    operator fun invoke() = get()

    override fun toString() = get().toPlainText()!!

    companion object {
        fun of(comp: BaseComponent) = Immediate(comp)
    }

    class Immediate(private val comp: BaseComponent) : ChatComponent() {
        override fun get() = comp
    }
}
