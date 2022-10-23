package info.voidev.mctest.engine.discovery

import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import java.lang.reflect.AnnotatedElement

abstract class McTestDescriptor(
    uniqueId: UniqueId,
    displayName: String,
    val element: AnnotatedElement,
    source: TestSource? = null,
) :
    AbstractTestDescriptor(uniqueId, displayName, source)
