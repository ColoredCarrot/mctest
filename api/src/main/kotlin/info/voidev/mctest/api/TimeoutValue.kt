package info.voidev.mctest.api

import java.time.Duration
import java.time.Instant

/**
 * Represents some measure of a duration.
 */
interface TimeoutValue<T> {

    /**
     * Returns a tracker that can later be used to check whether
     */
    fun begin(): T

    /**
     * Should be invoked every tick for trackers for which [hasElapsed] returns `false`.
     */
    fun tick(tracker: T): T = tracker

    /**
     * Checks whether the duration has elapsed, based on the given tracker.
     */
    fun hasElapsed(tracker: T): Boolean

    @JvmInline
    value class RealTime(private val duration: Duration) : TimeoutValue<Instant> {
        override fun begin(): Instant = Instant.now()

        override fun hasElapsed(tracker: Instant) = Duration.between(tracker, Instant.now()) >= duration
    }

    @JvmInline
    value class Deadline(private val instant: Instant) : TimeoutValue<Unit> {
        override fun begin() = Unit

        override fun hasElapsed(tracker: Unit) = Instant.now() >= instant
    }

    @JvmInline
    value class TickAmount(private val ticks: Int) : TimeoutValue<Int> {
        override fun begin() = 0

        override fun tick(tracker: Int) = tracker + 1

        override fun hasElapsed(tracker: Int) = tracker >= ticks
    }
}
