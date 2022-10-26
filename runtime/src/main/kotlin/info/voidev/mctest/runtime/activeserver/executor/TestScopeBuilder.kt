package info.voidev.mctest.runtime.activeserver.executor

import info.voidev.mctest.api.TestScope
import info.voidev.mctest.api.TickFunctionScope
import info.voidev.mctest.api.yieldTicks
import info.voidev.mctest.runtime.activeserver.lib.testplayer.PhysicalTestPlayer
import info.voidev.mctest.runtime.activeserver.lib.testplayer.TestPlayerService
import info.voidev.mctest.runtime.activeserver.lib.testplayer.TestPlayerSpec

class TestScopeBuilder(
    private val tickScope: TickFunctionScope,
    private val testPlayerService: TestPlayerService,
) : TestScope, TickFunctionScope by tickScope {

    private val _testPlayers = ArrayList<PhysicalTestPlayer>()

    val testPlayers: List<PhysicalTestPlayer> get() = _testPlayers

    suspend fun newTestPlayer(spec: TestPlayerSpec) =
        testPlayerService.join(spec, this).also(_testPlayers::add)

    override suspend fun syncPackets() {
        for (testPlayer in _testPlayers) {
            testPlayer.syncOwnPackets(tickScope)
        }

        // TODO: Remove this wonkiness when the async chat handling is dealt with
        yieldTicks(2)
        for (testPlayer in _testPlayers) {
            testPlayer.syncOwnPackets(tickScope)
        }
    }

}
