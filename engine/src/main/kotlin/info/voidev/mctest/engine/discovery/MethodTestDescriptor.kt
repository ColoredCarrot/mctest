package info.voidev.mctest.engine.discovery

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.MethodSource
import java.lang.reflect.Method

/**
 * Test descriptor for a test method annotated with `@MCTest`.
 */
class MethodTestDescriptor(
    uniqueId: UniqueId,
    val method: Method,
) : McTestDescriptor(uniqueId, method.name, MethodSource.from(method)) {

    override fun getType() = TestDescriptor.Type.TEST

    companion object {
        const val SEGMENT_TYPE = "method"
    }
}
