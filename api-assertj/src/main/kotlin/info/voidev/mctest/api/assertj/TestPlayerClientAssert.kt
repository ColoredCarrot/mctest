package info.voidev.mctest.api.assertj

import info.voidev.mctest.api.TestPlayerClient
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.ListAssert
import org.assertj.core.api.StringAssert
import java.util.function.Consumer

class TestPlayerClientAssert(actual: TestPlayerClient) :
    AbstractAssert<TestPlayerClientAssert, TestPlayerClient>(actual, TestPlayerClientAssert::class.java) {

    fun receivedMessages(): ListAssert<String> =
        ListAssert.assertThatList(actual.receivedMessages.toList())

    inline fun hasReceivedMessageThat(crossinline asserter: StringAssert.() -> Unit): TestPlayerClientAssert {
        receivedMessages().anySatisfy(Consumer {
            asserter(StringAssert(it))
        })
        return this
    }

}
