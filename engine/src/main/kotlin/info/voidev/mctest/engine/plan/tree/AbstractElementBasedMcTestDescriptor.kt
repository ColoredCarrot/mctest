package info.voidev.mctest.engine.plan.tree

import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import java.lang.reflect.AnnotatedElement

abstract class AbstractElementBasedMcTestDescriptor(
    uniqueId: UniqueId,
    displayName: String,
    val element: AnnotatedElement,
    source: TestSource? = null,
) : AbstractMcTestDescriptor(uniqueId, displayName, source)
