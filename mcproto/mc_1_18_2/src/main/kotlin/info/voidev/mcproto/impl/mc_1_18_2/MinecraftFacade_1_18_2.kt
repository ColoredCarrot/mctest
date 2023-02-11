package info.voidev.mcproto.impl.mc_1_18_2

import info.voidev.mcproto.api.MinecraftFacade

@Suppress("unused") // Used via reflection
class MinecraftFacade_1_18_2 : MinecraftFacade {
    override val clientFactory = MinecraftClientFactory_1_18_2()
}
