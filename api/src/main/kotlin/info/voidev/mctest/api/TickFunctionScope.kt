package info.voidev.mctest.api

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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
suspend inline fun <R : Any, T> TickFunctionScope.yieldTicksUntilNotNull(
    timeout: TimeoutValue<T>,
    getter: () -> R?,
): R {
    var timeoutTracker = timeout.begin()
    var value = getter()
    while (value == null) {
        if (timeout.hasElapsed(timeoutTracker)) {
            throw TimeoutException()
        }

        yieldTick()
        timeoutTracker = timeout.tick(timeoutTracker)

        value = getter()
    }

    return value
}

suspend fun TickFunctionScope.yieldTicksFor(time: Long, unit: TimeUnit) {
    val waitMillis = unit.toMillis(time)
    val startTime = System.currentTimeMillis()
    yieldTicksUntil { System.currentTimeMillis() - startTime >= waitMillis }
}

suspend fun TickFunctionScope.yieldTicks(n: Int) {
    repeat(n) {
        yieldTick()
    }
}
