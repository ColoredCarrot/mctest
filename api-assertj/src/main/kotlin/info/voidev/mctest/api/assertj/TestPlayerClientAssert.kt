package info.voidev.mctest.api.assertj

import info.voidev.mctest.api.testplayer.TestPlayerClient
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.ListAssert
import org.assertj.core.api.StringAssert
import java.util.function.Consumer

class TestPlayerClientAssert(actual: TestPlayerClient) :
    AbstractAssert<TestPlayerClientAssert, TestPlayerClient>(actual, TestPlayerClientAssert::class.java) {

    fun receivedMessages(): ListAssert<String> =
        ListAssert.assertThatList(actual.receivedMessages.map { it.message().toPlainText() })

    inline fun hasReceivedMessageThat(crossinline asserter: StringAssert.() -> Unit): TestPlayerClientAssert {
        receivedMessages().anySatisfy(Consumer {
            asserter(StringAssert(it))
        })
        return this
    }

    inline fun hasNotReceivedMessageThat(crossinline asserter: StringAssert.() -> Unit): TestPlayerClientAssert {
        receivedMessages().noneSatisfy(Consumer {
            asserter(StringAssert(it))
        })
        return this
    }

}
