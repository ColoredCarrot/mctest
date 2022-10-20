package info.voidev.mctest.api.testplayer

class TestPlayerDisconnectedException(
    message: String? = "A test player disconnected unexpectedly",
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    constructor(player: TestPlayer) : this(player.name + " disconnected unexpectedly")
}
