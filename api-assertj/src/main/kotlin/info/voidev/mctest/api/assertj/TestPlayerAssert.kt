package info.voidev.mctest.api.assertj

import info.voidev.mctest.api.testplayer.TestPlayer
import org.assertj.core.api.AbstractAssert

class TestPlayerAssert(actual: TestPlayer) : AbstractAssert<TestPlayerAssert, TestPlayer>(actual, TestPlayerAssert::class.java) {

    val client get() = TestPlayerClientAssert(actual.client)

}
