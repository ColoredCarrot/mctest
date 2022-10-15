package info.voidev.mctest.api

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.time.Duration

interface TickFunctionScope {
    /**
     * Suspends execution of the tick function.
     * Resumes at next server tick.
     */
    suspend fun yieldTick()
}

suspend inline fun TickFunctionScope.yieldTicksWhile(condition: () -> Boolean) {
    while (condition()) {
        yieldTick()
    }
}

suspend inline fun TickFunctionScope.yieldTicksUntil(condition: () -> Boolean) =
    yieldTicksWhile { !condition() }

/**
 * Convenience function for an often-used idiom:
 * waiting until a certain variable is not null.
 *
 * For example, you might want to wait
 * until the server or client has received an expected packet.
 */
suspend inline fun <T : Any> TickFunctionScope.yieldTicksUntilNotNull(timeoutTicks: Int = 5 * 20, getter: () -> T?): T {
    var elapsedTicks = 0
    var value = getter()
    while (value == null) {
        if (elapsedTicks >= timeoutTicks) {
            throw TimeoutException()
        }

        yieldTick()
        value = getter()
        ++elapsedTicks
    }

    return value
}

suspend fun TickFunctionScope.yieldTicksFor(time: Long, unit: TimeUnit) {
    val waitMillis = unit.toMillis(time)
    val startTime = System.currentTimeMillis()
    yieldTicksUntil { System.currentTimeMillis() - startTime >= waitMillis }
}

suspend fun TickFunctionScope.yieldTicksFor(duration: Duration) =
    yieldTicksFor(duration.inWholeMilliseconds, TimeUnit.MILLISECONDS)

suspend fun TickFunctionScope.yieldTicks(n: Int) {
    repeat(n) {
        yieldTick()
    }
}
