package info.voidev.mctest.engine.discovery

import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor

abstract class McTestDescriptor(uniqueId: UniqueId, displayName: String, source: TestSource? = null) :
    AbstractTestDescriptor(uniqueId, displayName, source)
