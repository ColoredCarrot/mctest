package info.voidev.mctest.api

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Standard set of assertions shipped with MCTest.
 * You are welcome to use any other assertion library,
 * like AssertJ or Hamcrest--or even JUnit Jupiter.
 */
@OptIn(ExperimentalContracts::class)
object Assertions {

    @JvmStatic
    @JvmOverloads
    fun fail(message: String? = null, cause: Throwable? = null): Nothing {
        throw AssertionFailedException(message, cause)
    }

    @JvmStatic
    inline fun fail(cause: Throwable? = null, messageSupplier: () -> String?): Nothing {
        fail(messageSupplier(), cause)
    }

    @JvmStatic
    inline fun assertTrue(value: Boolean, messageSupplier: () -> String? = { null }) {
        if (!value) {
            fail(messageSupplier = messageSupplier)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun assertEquals(expected: Any?, actual: Any?, message: String? = null) {
        assertEquals(expected, actual) { message }
    }

    @JvmStatic
    inline fun assertEquals(expected: Any?, actual: Any?, messageSupplier: () -> String?) {
        assertTrue(expected == actual) { messageSupplier() ?: "Expected: $expected  Got: $actual" }
    }

    @JvmStatic
    @JvmOverloads
    fun assertNull(actual: Any?, message: String? = null) {
        assertNull(actual) { message }
    }

    @JvmStatic
    inline fun assertNull(actual: Any?, messageSupplier: () -> String?) {
        contract {
            returns() implies (actual == null)
        }

        assertEquals(null, actual, messageSupplier)
    }

    @JvmStatic
    @JvmOverloads
    fun assertNotNull(actual: Any?, message: String? = null) {
        assertNotNull(actual) { message }
    }

    @JvmStatic
    inline fun assertNotNull(actual: Any?, messageSupplier: () -> String?) {
        contract {
            returns() implies (actual != null)
        }

        assertTrue(actual != null) { messageSupplier() ?: "Expected value to be non-null" }
    }
}
