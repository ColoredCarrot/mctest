package info.voidev.mctest.example

import info.voidev.mctest.api.MCTest
import info.voidev.mctest.api.MCTestPlayer
import info.voidev.mctest.api.TestPlayer
import info.voidev.mctest.api.TestScope
import info.voidev.mctest.api.assertj.assertThat

class PermissionsTest {

    @MCTest
    suspend fun TestScope.`non-op player`(
        @MCTestPlayer(op = false) player: TestPlayer,
    ) {
        player.client.say("/forward")
        assertThat(player.client).hasReceivedMessageThat { containsIgnoringCase("permission") }
    }

    @MCTest
    suspend fun TestScope.`non-op player with specific permission`(
        @MCTestPlayer(
            op = false,
            permissions = ["example.forward"],
        ) player: TestPlayer,
    ) {
        player.client.say("/forward")
        assertThat(player.client).hasNotReceivedMessageThat { containsIgnoringCase("permission") }
    }
}
