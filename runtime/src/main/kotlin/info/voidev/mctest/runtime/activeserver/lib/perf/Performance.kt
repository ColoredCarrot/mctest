package info.voidev.mctest.runtime.activeserver.lib.perf

import info.voidev.mctest.runtime.activeserver.lib.tickable.Tickable
import info.voidev.mctest.runtime.activeserver.lib.tickable.TickableRegistry
import org.bukkit.Bukkit
import kotlin.time.Duration.Companion.milliseconds

object Performance {

    var currentTick = 1
        private set

    init {
        TickableRegistry.register(object : Tickable {
            override fun tick() {
                ++currentTick
            }
        })
    }

    inline fun <R> section(title: String, block: () -> R): R {
        val startTick = currentTick
        val startTime = System.currentTimeMillis()

        try {
            return block()
        } finally {
//            logger.info(
//                "\nPerformance: [%s]\n - Real time: %s\n - Server time: %d ticks".format(
//                    title,
//                    (System.currentTimeMillis() - startTime).milliseconds.toString(),
//                    currentTick - startTick,
//                )
//            )
        }
    }

    @PublishedApi
    internal val logger = Bukkit.getLogger()
}
