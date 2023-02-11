package info.voidev.mctest.api.assertj

import info.voidev.mcproto.api.TabList
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ThrowingConsumer
import java.util.function.Consumer

class TabListAssert(
    actual: TabList,
) : AbstractObjectAssert<TabListAssert, TabList>(actual, TabListAssert::class.java) {

    fun hasExactlyEntriesSatisfying(vararg assertions: ThrowingConsumer<in TabList.Entry>): TabListAssert {
        assertThat(actual.toList()).satisfiesExactlyInAnyOrder(*assertions)
        return this
    }

    fun hasExactlyEntriesSatisfying(vararg assertions: TabListEntryAssert.() -> Unit): TabListAssert {
        assertThat(actual.toList()).satisfiesExactlyInAnyOrder(
            *assertions
                .map { assertion -> Consumer<TabList.Entry> { assertion(TabListEntryAssert(it)) } }
                .toTypedArray()
        )
        return this
    }

}
