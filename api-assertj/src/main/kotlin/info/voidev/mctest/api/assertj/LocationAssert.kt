package info.voidev.mctest.api.assertj

import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.assertj.core.data.Percentage
import org.bukkit.Location

class LocationAssert(
    actual: Location,
) : AbstractObjectAssert<LocationAssert, Location>(actual, LocationAssert::class.java) {

    fun isCloseTo(loc: Location, percentage: Percentage): LocationAssert {
        assertThat(actual.distance(loc)).isCloseTo(0.0, percentage)
        return this
    }

    fun isCloseTo(loc: Location, offset: Offset<Double>): LocationAssert {
        assertThat(actual.distance(loc)).isCloseTo(0.0, offset)
        return this
    }

}
