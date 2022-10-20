package info.voidev.mctest.example

import info.voidev.mctest.api.MCTest
import info.voidev.mctest.api.testplayer.TestPlayer
import info.voidev.mctest.api.assertj.assertThat

class TabListTest {

    @MCTest
    fun `player is visible in tab list`(
        alice: TestPlayer,
        bob: TestPlayer,
    ) {
        for (player in listOf(alice, bob)) {
            assertThat(player.client.tabList).hasExactlyEntriesSatisfying(
                { tabListEntry ->
                    assertThat(tabListEntry)
                        .nameAndDisplayName()
                        .isEqualTo("Alice")
                },
                { tabListEntry ->
                    assertThat(tabListEntry)
                        .nameAndDisplayName()
                        .isEqualTo("Bob")
                },
            )
        }
    }

}
