@file:JvmName("Assertions")

package info.voidev.mctest.api.assertj

import info.voidev.mctest.api.TestPlayer
import info.voidev.mctest.api.TestPlayerClient
import info.voidev.mctest.api.TestScope
import info.voidev.mctest.api.testplayer.ClientTabList
import org.assertj.core.api.AbstractDoubleAssert
import org.assertj.core.api.DoubleAssert
import org.assertj.core.api.Assertions as NormalAssertions

suspend fun TestScope.assertThat(actual: TestPlayer): TestPlayerAssert {
    syncPackets()
    return TestPlayerAssert(actual)
}

suspend fun TestScope.assertThat(actual: TestPlayerClient): TestPlayerClientAssert {
    syncPackets()
    return TestPlayerClientAssert(actual)
}

fun assertThat(actual: ClientTabList) = ClientTabListAssert(actual)
fun assertThat(actual: ClientTabList.Entry) = ClientTabListEntryAssert(actual)

//fun assertThat(player: TestPlayer): TestPlayerAssert {
//    return TestPlayerAssert(player)
//}
