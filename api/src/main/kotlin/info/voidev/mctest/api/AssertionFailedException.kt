package info.voidev.mctest.api

class AssertionFailedException @JvmOverloads constructor(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
