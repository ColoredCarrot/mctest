package info.voidev.mctest.api.meta

import org.junit.platform.commons.annotation.Testable

/**
 * Tests or test classes can specify an acceptable Minecraft version range
 * using `@MCVersion`.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Testable
annotation class MCVersion(
    val min: String = "",
    val max: String = "",
)
