package info.voidev.mctest.engine.server.platform

class MalformedVersionException(
    val version: String,
    message: String,
    cause: Throwable? = null,
) : IllegalArgumentException("Version $version is malformed: $message", cause)
