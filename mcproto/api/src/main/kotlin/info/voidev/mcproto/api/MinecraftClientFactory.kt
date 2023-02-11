package info.voidev.mcproto.api

interface MinecraftClientFactory {

    fun create(playerName: String, port: Int, host: String = "localhost") : MinecraftClient

}
