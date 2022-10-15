package info.voidev.mctest.runtime.activeserver.lib.parambind

import info.voidev.mctest.api.TestScope
import info.voidev.mctest.runtime.activeserver.executor.TestScopeBuilder
import java.lang.reflect.Parameter

class TestScopeParamBinder : ParamBinderForClass<TestScope>(TestScope::class.java) {

    override suspend fun bind(param: Parameter, scopeBuilder: TestScopeBuilder): TestScope {
        return scopeBuilder
    }

}
