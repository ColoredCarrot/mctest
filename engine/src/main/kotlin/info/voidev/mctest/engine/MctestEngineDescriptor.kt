package info.voidev.mctest.engine

import info.voidev.mctest.runtimesdk.proto.MctestConfig
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor

class MctestEngineDescriptor(uniqueId: UniqueId, val config: MctestConfig) : EngineDescriptor(uniqueId, "Mctest") {
    companion object {
        const val ENGINE_ID = "mctest"
    }
}
