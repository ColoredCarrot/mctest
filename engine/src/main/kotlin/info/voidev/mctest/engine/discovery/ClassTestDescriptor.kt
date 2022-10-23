package info.voidev.mctest.engine.discovery

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource

class ClassTestDescriptor(
    uniqueId: UniqueId,
    val testClass: Class<*>,
) : McTestDescriptor(uniqueId, testClass.simpleName, testClass, ClassSource.from(testClass)) {

    override fun getType() = TestDescriptor.Type.CONTAINER

    companion object {
        const val SEGMENT_TYPE = "class"
    }
}
