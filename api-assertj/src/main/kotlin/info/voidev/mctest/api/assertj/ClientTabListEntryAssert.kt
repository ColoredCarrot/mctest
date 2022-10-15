package info.voidev.mctest.api.assertj

import info.voidev.mctest.api.testplayer.ClientTabList
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions.assertThat
import java.util.function.Consumer

class ClientTabListEntryAssert(
    actual: ClientTabList.Entry,
) : AbstractObjectAssert<ClientTabListEntryAssert, ClientTabList.Entry>(actual, ClientTabListEntryAssert::class.java) {

    fun nameAndDisplayName(): AbstractStringAssert<*> {
        displayNameIsNotDifferent()
        return assertThat(actual.name)
    }

    fun displayNameIsNotDifferent() {
        assertThat(actual.displayNameText).satisfiesAnyOf(
            Consumer { assertThat(it).isNull() },
            Consumer { assertThat(it).isEqualTo(actual.name) },
        )
    }

}
