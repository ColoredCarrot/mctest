package info.voidev.mctest.api

/**
 * `@Disabled` is used to signal that the annotated test method or
 * test class is currently <em>disabled</em> and should not be executed.
 *
 * `@Disabled` may optionally be declared with a [reason][value]
 * to document why the annotated test class or test method is disabled.
 *
 * When applied at the class level, all test methods within that class
 * are automatically disabled as well.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Disabled(
    /**
     * The reason the annotated test method or test class is disabled.
     */
    val value: String = "",
)
