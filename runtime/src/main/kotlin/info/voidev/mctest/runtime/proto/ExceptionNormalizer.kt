package info.voidev.mctest.runtime.proto

import info.voidev.mctest.runtimesdk.proto.EngineService
import info.voidev.mctest.runtimesdk.proto.NonMarshalableException

/**
 * If RMI method calls throw an exception,
 * RMI marshals that exception, transfers it,
 * and unmarshals it in the engine.
 *
 * This is a problem for exceptions whose class the engine does not know about.
 * As such, they have to be translated into another class.
 */
class ExceptionNormalizer(private val engine: EngineService) {

    fun normalize(t: Throwable): Throwable {
        var anyChanges = false

        val normalizedCause = t.cause?.let(this::normalize)
        anyChanges = anyChanges || normalizedCause !== t.cause

        val normalizedSuppressed = t.suppressed.map {
            val normalized = normalize(it)
            anyChanges = anyChanges || normalized !== it
            normalized
        }

        return if (engine.maySendClass(t.javaClass.name) && !anyChanges) {
            t
        } else {
            val ex = NonMarshalableException(t.javaClass.name, t.message, normalizedCause)
            ex.stackTrace = t.stackTrace
            normalizedSuppressed.forEach { ex.addSuppressed(it as Throwable?) }
            ex
        }
    }

}
