package info.voidev.mctest.api

import org.junit.platform.commons.annotation.Testable

/**
 * Methods marked with this annotation are MCTest test methods.
 *
 * Such methods are executed in a different JVM in a Spigot server.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Testable
annotation class MCTest(
    val serverScope: TestServerScope = TestServerScope.GLOBAL,
)
