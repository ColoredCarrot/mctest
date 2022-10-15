package info.voidev.mctest.runtime.activeserver.lib.parambind

import info.voidev.mctest.runtime.activeserver.executor.TestScopeBuilder
import java.lang.reflect.Parameter

class CollectiveParamBinder(private val binders: List<ParamBinder>) : ParamBinder {

    override fun canBind(param: Parameter, scopeBuilder: TestScopeBuilder) =
        binders.any { it.canBind(param, scopeBuilder) }

    override suspend fun bind(param: Parameter, scopeBuilder: TestScopeBuilder) =
        binders.first { it.canBind(param, scopeBuilder) }.bind(param, scopeBuilder)

}
