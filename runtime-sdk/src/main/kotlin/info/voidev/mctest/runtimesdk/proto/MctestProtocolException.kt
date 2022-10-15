package info.voidev.mctest.runtimesdk.proto

import java.lang.RuntimeException

open class MctestProtocolException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)