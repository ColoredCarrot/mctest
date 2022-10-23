package info.voidev.mctest.engine.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TreeUtilTest {

    @Test
    fun `traverse tree`() {
        val flat = traverseTree(
            root = 0,
            getChildren = {
                when (it) {
                    0 -> sequenceOf(-2, 4)
                    -2 -> sequenceOf(-9, -8, -7)
                    4 -> sequenceOf(5)
                    5 -> sequenceOf(6)
                    else -> emptySequence()
                }
            }
        ).toList()
        assertEquals(listOf(0, 4, 5, 6, -2, -7, -8, -9), flat)
    }
}
