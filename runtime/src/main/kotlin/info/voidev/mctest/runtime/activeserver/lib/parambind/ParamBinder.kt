package info.voidev.mctest.runtime.activeserver.lib.parambind

import info.voidev.mctest.runtime.activeserver.executor.TestScopeBuilder
import java.lang.reflect.Parameter

interface ParamBinder {

    fun canBind(param: Parameter, scopeBuilder: TestScopeBuilder): Boolean

    suspend fun bind(param: Parameter, scopeBuilder: TestScopeBuilder): Any?

}
