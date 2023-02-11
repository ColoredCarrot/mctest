@file:JvmName("Assertions")

package info.voidev.mctest.api.assertj

import info.voidev.mcproto.api.TabList
import info.voidev.mctest.api.TestScope
import info.voidev.mctest.api.testplayer.TestPlayer
import info.voidev.mctest.api.testplayer.TestPlayerClient
import org.bukkit.Location

suspend fun TestScope.assertThat(actual: TestPlayer): TestPlayerAssert {
    syncPackets()
    return TestPlayerAssert(actual)
}

suspend fun TestScope.assertThat(actual: TestPlayerClient): TestPlayerClientAssert {
    syncPackets()
    return TestPlayerClientAssert(actual)
}

fun assertThat(actual: TabList) = TabListAssert(actual)
fun assertThat(actual: TabList.Entry) = TabListEntryAssert(actual)

fun assertThat(actual: Location) = LocationAssert(actual)
