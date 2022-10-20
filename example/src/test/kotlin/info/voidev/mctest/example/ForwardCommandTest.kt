package info.voidev.mctest.example

import info.voidev.mctest.api.MCTest
import info.voidev.mctest.api.MCTestPlayer
import info.voidev.mctest.api.TestPlayer
import info.voidev.mctest.api.TestScope
import info.voidev.mctest.api.assertj.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.bukkit.Bukkit

class ForwardCommandTest {

    @MCTest
    suspend fun TestScope.`player teleports themself forward`(
        player: TestPlayer,
    ) {
        val oldLocation = player.location

        player.client.say("/forward")

        syncPackets()
        assertThat(oldLocation.distance(player.location))
            .isCloseTo(1.0, withinEpsilon)
    }

    @MCTest
    suspend fun TestScope.`player teleports themself forward by amount`(
        player: TestPlayer,
    ) {
        val oldLocation = player.location

        player.client.say("/forward 3")

        syncPackets()
        assertThat(oldLocation.distance(player.location))
            .isCloseTo(3.0, withinEpsilon)
    }

    @MCTest
    suspend fun TestScope.`console teleports player forward by amount`(
        @MCTestPlayer(name = "John") player: TestPlayer,
    ) {
        val oldLocation = player.location

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "forward 4 John")

        syncPackets()
        assertThat(oldLocation.distance(player.location))
            .isCloseTo(4.0, withinEpsilon)
    }

    @MCTest
    suspend fun TestScope.`forward by invalid number`(
        @MCTestPlayer(name = "John") player: TestPlayer,
    ) {
        val oldLocation = player.location

        player.client.say("/forward nan")

        assertThat(player.client).hasReceivedMessageThat { isEqualTo("Not a valid number") }
        assertThat(player.location).isCloseTo(oldLocation, withinEpsilon)
    }

    companion object {
        private const val EPSILON = 1e-7

        private val withinEpsilon = within(EPSILON)
    }
}
