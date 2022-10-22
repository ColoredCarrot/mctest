package info.voidev.mctest.example

import info.voidev.mctest.api.MCTest
import info.voidev.mctest.api.assertj.assertThat
import info.voidev.mctest.api.testplayer.TestPlayer

class TabListTest {

    @MCTest
    fun `player is visible in tab list`(
        alice: TestPlayer,
        bob: TestPlayer,
    ) {
        for (player in listOf(alice, bob)) {
            assertThat(player.client.tabList).hasExactlyEntriesSatisfying(
                { nameAndDisplayName().isEqualTo("Alice") },
                { nameAndDisplayName().isEqualTo("Bob") },
            )
        }
    }

}
