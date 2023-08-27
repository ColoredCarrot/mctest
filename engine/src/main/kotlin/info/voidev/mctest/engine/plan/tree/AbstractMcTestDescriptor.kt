package info.voidev.mctest.engine.plan.tree

import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor

abstract class AbstractMcTestDescriptor(
    uniqueId: UniqueId,
    displayName: String,
    source: TestSource? = null,
) : AbstractTestDescriptor(uniqueId, displayName, source)
