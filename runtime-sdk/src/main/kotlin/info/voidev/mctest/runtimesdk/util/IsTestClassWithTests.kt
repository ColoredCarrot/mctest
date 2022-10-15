package info.voidev.mctest.runtimesdk.util

import org.junit.platform.commons.util.ReflectionUtils.isInnerClass
import org.junit.platform.commons.util.ReflectionUtils.isMethodPresent
import java.lang.reflect.Modifier.isAbstract
import java.lang.reflect.Modifier.isPrivate
import java.util.function.Predicate

object IsTestClassWithTests : Predicate<Class<*>> {
    override fun test(c: Class<*>): Boolean {
        // Test class must not be private or abstract...
        if (isPrivate(c.modifiers) || isAbstract(c.modifiers)) {
            return false
        }

        // ...or local, anonymous or inner...
        if (c.isLocalClass || c.isAnonymousClass || isInnerClass(c)) {
            return false
        }

        /// ...and it must contain at least one test...
        if (!isMethodPresent(c, IsMcTestMethod)) {
            return false
        }

        // ...and have a public no-args constructor
        try {
            c.getConstructor()
        } catch (_: NoSuchMethodException) {
            return false
        }

        return true
    }
}
