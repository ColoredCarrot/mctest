package info.voidev.mcproto.impl.mc_1_18_2

import info.voidev.mcproto.api.MinecraftClient
import info.voidev.mcproto.api.MinecraftClientFactory

class MinecraftClientFactory_1_18_2 : MinecraftClientFactory {

    override fun create(playerName: String, port: Int, host: String): MinecraftClient {
        return MinecraftClient_1_18_2(host, port, playerName)
    }
}
