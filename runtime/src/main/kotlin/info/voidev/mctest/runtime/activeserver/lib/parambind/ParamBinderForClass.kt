package info.voidev.mctest.runtime.activeserver.lib.parambind

import info.voidev.mctest.runtime.activeserver.executor.TestScopeBuilder
import java.lang.reflect.Parameter

abstract class ParamBinderForClass<T>(private val cls: Class<T>) : ParamBinder {

    override fun canBind(param: Parameter, scopeBuilder: TestScopeBuilder) = param.type == cls

    abstract override suspend fun bind(param: Parameter, scopeBuilder: TestScopeBuilder): T

}
