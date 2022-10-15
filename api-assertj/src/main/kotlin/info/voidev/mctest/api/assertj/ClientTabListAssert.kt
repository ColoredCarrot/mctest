package info.voidev.mctest.api.assertj

import info.voidev.mctest.api.testplayer.ClientTabList
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ThrowingConsumer

class ClientTabListAssert(
    actual: ClientTabList,
) : AbstractObjectAssert<ClientTabListAssert, ClientTabList>(actual, ClientTabListAssert::class.java) {

    fun hasExactlyEntriesSatisfying(vararg assertions: ThrowingConsumer<in ClientTabList.Entry>): ClientTabListAssert {
        assertThat(actual.entries).satisfiesExactlyInAnyOrder(*assertions)
        return this
    }

}
