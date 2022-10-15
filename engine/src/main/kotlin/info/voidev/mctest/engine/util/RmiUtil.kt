package info.voidev.mctest.engine.util

import java.rmi.ServerError
import java.rmi.UnexpectedException

/**
 * If a method call throws a checked exception
 * that is not part of the callee's throws declaration list,
 * RMI throws an [UnexpectedException].
 *
 * Since Kotlin has done away with checked exceptions,
 * this function unwraps that exception.
 */
inline fun <R> unwrapRmiExceptions(callMethod: () -> R): R {
    try {
        return callMethod()
    } catch (ex: UnexpectedException) {
        throw ex.cause ?: ex
    } catch (ex: ServerError) {
        throw ex.cause ?: ex
    }
}
