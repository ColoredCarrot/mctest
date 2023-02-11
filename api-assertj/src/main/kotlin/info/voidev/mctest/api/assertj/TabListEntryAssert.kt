package info.voidev.mctest.api.assertj

import info.voidev.mcproto.api.TabList
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions.assertThat
import java.util.function.Consumer

class TabListEntryAssert(
    actual: TabList.Entry,
) : AbstractObjectAssert<TabListEntryAssert, TabList.Entry>(actual, TabListEntryAssert::class.java) {

    fun nameAndDisplayName(): AbstractStringAssert<*> {
        displayNameIsNotDifferent()
        return assertThat(actual.name)
    }

    fun displayNameIsNotDifferent() {
        assertThat(actual.displayName?.get()?.toPlainText()).satisfiesAnyOf(
            Consumer { assertThat(it).isNull() },
            Consumer { assertThat(it).isEqualTo(actual.name) },
        )
    }

}
