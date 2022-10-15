package info.voidev.mctest.example

import info.voidev.mctest.api.Assertions.assertTrue
import info.voidev.mctest.api.MCTest
import info.voidev.mctest.api.MCTestPlayer
import info.voidev.mctest.api.TestPlayer
import info.voidev.mctest.api.TestScope
import info.voidev.mctest.api.assertj.assertThat
import org.bukkit.Bukkit

class ChatTest {

    @MCTest
    suspend fun TestScope.`player receives chat`(
        @MCTestPlayer(name = "John") john: TestPlayer,
    ) {
        assertTrue(Bukkit.isPrimaryThread())

        john.sendMessage("Hello, world!")

        assertThat(john.client)
            .hasReceivedMessageThat { contains("Hello, world!") }
    }

    @MCTest
    suspend fun TestScope.`player chat is visible to other player`(
        alice: TestPlayer,
        bob: TestPlayer,
    ) {
        alice.client.say("Hello, world!")

        assertThat(bob.client)
            .hasReceivedMessageThat { contains("Alice", "Hello, world!") }
        assertThat(alice.client)
            .hasReceivedMessageThat { contains("Hello, world!") }
    }

    @MCTest
    suspend fun TestScope.`player chat is visible to multiple other players`(
        alice: TestPlayer,
        bob: TestPlayer,
        charlie: TestPlayer,
    ) {
        alice.client.say("Hello, world!")

        assertThat(bob).client
            .hasReceivedMessageThat { contains("Alice", "Hello, world!") }
        assertThat(charlie).client
            .hasReceivedMessageThat { contains("Alice", "Hello, world!") }
    }

}
