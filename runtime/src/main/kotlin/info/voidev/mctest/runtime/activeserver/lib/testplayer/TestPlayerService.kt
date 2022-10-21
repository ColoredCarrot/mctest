package info.voidev.mctest.runtime.activeserver.lib.testplayer

import info.voidev.mctest.api.TestScope
import info.voidev.mctest.api.TimeoutValue
import info.voidev.mctest.api.yieldTicksUntilNotNull
import info.voidev.mctest.runtime.activeserver.testeePluginInstance
import info.voidev.mctest.runtimesdk.proto.MctestConfig
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration

class TestPlayerService(
    private val mctestConfig: MctestConfig,
) {

    suspend fun join(spec: TestPlayerSpec, scope: TestScope): PhysicalTestPlayer {
        require(Bukkit.isPrimaryThread())

        val client = PhysicalTestPlayerClient(spec, Bukkit.getPort(), scope)

        // Connect on a separate thread so as not to block the primary thread
        // (we will be yielding ticks instead)
        Bukkit.getScheduler().runTaskAsynchronously(testeePluginInstance, Runnable {
            client.connect()
        })

        val joinTimeout = TimeoutValue.RealTime(Duration.ofMillis(mctestConfig.testPlayerJoinTimeoutMs))
        val player = scope.yieldTicksUntilNotNull(joinTimeout) { Bukkit.getPlayerExact(spec.name) }

        player.isOp = spec.op
        setUpPermissions(player, spec.permissions)

        return PhysicalTestPlayer(player, client)
    }

    private fun setUpPermissions(player: Player, permissions: List<String>) {
        val attachment = player.addAttachment(testeePluginInstance)
        for (permission in permissions) {
            attachment.setPermission(permission, true)
        }
    }
}
