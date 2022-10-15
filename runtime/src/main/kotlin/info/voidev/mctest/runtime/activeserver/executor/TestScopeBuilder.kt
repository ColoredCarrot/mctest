package info.voidev.mctest.runtime.activeserver.executor

import info.voidev.mctest.api.TestScope
import info.voidev.mctest.api.TickFunctionScope
import info.voidev.mctest.runtime.activeserver.lib.testplayer.PhysicalTestPlayer
import info.voidev.mctest.runtime.activeserver.lib.testplayer.TestPlayerService
import info.voidev.mctest.runtime.activeserver.lib.testplayer.TestPlayerSpec

class TestScopeBuilder(
    private val tickScope: TickFunctionScope,
) : TestScope, TickFunctionScope by tickScope {

    private val testPlayers = ArrayList<PhysicalTestPlayer>()

    suspend fun newTestPlayer(spec: TestPlayerSpec) =
        TestPlayerService.join(spec, tickScope).also(testPlayers::add)

    override suspend fun syncPackets() {
        // We also have a "grace tick" before and after
        yieldTick()
        for (testPlayer in testPlayers) {
            testPlayer.awaitServerboundPackets(this)
            testPlayer.awaitClientboundPackets(this)
        }
        yieldTick()
    }

}
