package info.voidev.mctest.runtime.activeserver.lib.testplayer

import info.voidev.mctest.api.TestScope
import info.voidev.mctest.api.testplayer.TestPlayerClient
import info.voidev.mctest.runtime.activeserver.MinecraftVersionFacade
import org.bukkit.Bukkit

class PhysicalTestPlayerClient(spec: TestPlayerSpec, port: Int, private val scope: TestScope) : TestPlayerClient {

    private val client = MinecraftVersionFacade.minecraftFacade.clientFactory.create(spec.name, port)

    fun connect() {
        client.connect()
    }

    fun disconnect() {
        client.disconnect()
    }

    override suspend fun say(message: String) {
        assert(Bukkit.isPrimaryThread())

        client.say(message)
        scope.syncPackets()
    }

    val receivedPacketsCount get() = client.numReceivedPackets

    val sentPacketsCount get() = client.numSentPackets

    override val receivedMessages get() = client.receivedMessages

    override val tabList get() = client.tabList
}
