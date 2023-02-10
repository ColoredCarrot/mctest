package info.voidev.mctest.runtime.activeserver.executor

import info.voidev.mctest.runtimesdk.util.IsTestClassWithTests
import java.lang.reflect.InvocationTargetException

class TestClassInstanceCache : ClassValue<Any>() {

    override fun computeValue(type: Class<*>): Any {
        require(IsTestClassWithTests.test(type)) { "Attempted to get test instance for non-test $type" }

        try {
            return type.getConstructor().newInstance()
        } catch (ex: InvocationTargetException) {
            throw ex.cause ?: ex
        }
    }

}
