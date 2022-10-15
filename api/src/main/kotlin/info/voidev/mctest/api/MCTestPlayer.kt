package info.voidev.mctest.api

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MCTestPlayer(
    val name: String = "",
)
