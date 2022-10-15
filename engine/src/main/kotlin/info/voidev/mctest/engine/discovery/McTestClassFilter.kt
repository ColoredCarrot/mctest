package info.voidev.mctest.engine.discovery

import org.junit.platform.commons.util.ReflectionUtils
import java.lang.reflect.Modifier
import java.util.function.Predicate

object McTestClassFilter : Predicate<Class<*>> {
    override fun test(c: Class<*>): Boolean {
        // Test classes must be public and non-abstract
        if (!Modifier.isPublic(c.modifiers) || Modifier.isAbstract(c.modifiers)) {
            return false
        }

        // Test classes must not be non-static inner
        if (ReflectionUtils.isInnerClass(c)) {
            return false
        }

        return true
    }
}
