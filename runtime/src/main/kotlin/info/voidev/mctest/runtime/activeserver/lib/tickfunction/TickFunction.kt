package info.voidev.mctest.runtime.activeserver.lib.tickfunction

import info.voidev.mctest.api.TickFunctionScope

/**
 * A function that is run on the primary thread and that can yield ticks.
 *
 * @see TickFunctionScope.yieldTick
 */
typealias TickFunction<R> = suspend TickFunctionScope.() -> R
