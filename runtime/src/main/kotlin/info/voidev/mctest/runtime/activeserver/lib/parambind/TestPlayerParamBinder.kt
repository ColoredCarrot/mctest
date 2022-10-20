package info.voidev.mctest.runtime.activeserver.lib.parambind

import info.voidev.mctest.api.testplayer.MCTestPlayer
import info.voidev.mctest.api.testplayer.TestPlayer
import info.voidev.mctest.runtime.activeserver.executor.TestScopeBuilder
import info.voidev.mctest.runtime.activeserver.lib.testplayer.PhysicalTestPlayer
import info.voidev.mctest.runtime.activeserver.lib.testplayer.TestPlayerSpec
import java.lang.reflect.Parameter

class TestPlayerParamBinder : ParamBinderForClass<TestPlayer>(TestPlayer::class.java) {

    override suspend fun bind(param: Parameter, scopeBuilder: TestScopeBuilder): PhysicalTestPlayer {
        val testPlayerAnnot = param.getAnnotation(MCTestPlayer::class.java) ?: MCTestPlayer()
        val testPlayerSpec = TestPlayerSpec(
            name = testPlayerAnnot.name.ifEmpty { getPlayerNameFromParamName(param.name) },
            op = testPlayerAnnot.op,
            permissions = testPlayerAnnot.permissions.asList(),
        )

        return scopeBuilder.newTestPlayer(testPlayerSpec)
    }

    private fun getPlayerNameFromParamName(name: String): String {
        return name[0].uppercaseChar() + name.substring(1)
    }

}
