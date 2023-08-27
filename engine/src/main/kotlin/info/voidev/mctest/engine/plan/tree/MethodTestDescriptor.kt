package info.voidev.mctest.engine.plan.tree

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
) : AbstractElementBasedMcTestDescriptor(uniqueId, method.name, method, MethodSource.from(method)) {

    override fun getType() = TestDescriptor.Type.CONTAINER

    companion object {
        const val SEGMENT_TYPE = "method"
    }
}
