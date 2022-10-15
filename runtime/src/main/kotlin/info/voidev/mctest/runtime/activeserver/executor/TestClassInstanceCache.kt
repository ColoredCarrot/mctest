package info.voidev.mctest.runtime.activeserver.executor

import info.voidev.mctest.runtimesdk.util.IsTestClassWithTests

class TestClassInstanceCache : ClassValue<Any>() {

    override fun computeValue(type: Class<*>): Any {
        require(IsTestClassWithTests.test(type)) { "Attempted to get test instance for non-test $type" }

        return type.newInstance()
    }

}
