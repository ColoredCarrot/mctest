package info.voidev.mctest.runtimesdk.proto

import java.lang.RuntimeException

class NonMarshalableException(
    originalClass: String,
    message: String?,
    cause: Throwable?,
) : RuntimeException("$originalClass: $message", cause)
