package info.voidev.mctest.runtime.activeserver.lib.tickfunction

import info.voidev.mctest.runtime.activeserver.lib.tickable.TickableRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TickFunctionTest {

    @Test
    fun `without yield finishes after one tick`() {
        val tickable = TickableRegistry()

        val future = launchTickFunction(tickable) {}

        assertThat(future).isNotCompleted()

        tickable.tick()

        assertThat(future).isCompletedWithValue(Unit)
    }

    @Test
    fun `one yield`() {
        val tickable = TickableRegistry()

        var counter = 0

        val future = launchTickFunction(tickable) {
            ++counter
            yieldTick()
            ++counter

            Unit
        }

        assertThat(counter).isEqualTo(0)
        assertThat(future).isNotCompleted()

        tickable.tick()
        assertThat(counter).isEqualTo(1)
        assertThat(future).isNotCompleted()

        tickable.tick()
        assertThat(counter).isEqualTo(2)
        assertThat(future).isCompletedWithValue(Unit)
    }

}
