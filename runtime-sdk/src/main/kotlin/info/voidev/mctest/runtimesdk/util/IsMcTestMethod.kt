package info.voidev.mctest.runtimesdk.util

import info.voidev.mctest.api.MCTest
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.function.Predicate
import kotlin.coroutines.Continuation

/**
 * A predicate that checks whether a method is an MCTest test method.
 */
object IsMcTestMethod : Predicate<Method> {

    override fun test(m: Method): Boolean {
        // Method must not be any of: static, private, abstract
        if (m.modifiers and (Modifier.STATIC or Modifier.PRIVATE or Modifier.ABSTRACT) != 0) {
            return false
        }

        if (!m.isAnnotationPresent(MCTest::class.java)) {
            return false
        }

        // There must be at most one Continuation parameter, and it must be the last parameter
        if (m.parameterCount > 0) {
            val paramTypes = m.parameterTypes.asList()
            for ((idx, paramType) in paramTypes.withIndex()) {
                if (idx != paramTypes.lastIndex && paramType == Continuation::class.java) {
                    return false
                }
            }
        }

        return true
    }

}
