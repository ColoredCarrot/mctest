package info.voidev.mctest.runtime.activeserver.lib.tickable

import java.util.concurrent.CopyOnWriteArrayList

open class TickableRegistry : Tickable {

    private val tickables = CopyOnWriteArrayList<Tickable>()

    fun register(tickable: Tickable) {
        tickables += tickable
    }

    fun unregister(tickable: Tickable) {
        tickables -= tickable
    }

    override fun tick() {
        tickables.forEach(Tickable::tick)
    }

    companion object Global : TickableRegistry()
}
