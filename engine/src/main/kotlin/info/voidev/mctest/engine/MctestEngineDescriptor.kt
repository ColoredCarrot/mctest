package info.voidev.mctest.engine

import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor

class MctestEngineDescriptor(uniqueId: UniqueId) : EngineDescriptor(uniqueId, "Mctest") {
    companion object {
        const val ENGINE_ID = "mctest"
    }
}
