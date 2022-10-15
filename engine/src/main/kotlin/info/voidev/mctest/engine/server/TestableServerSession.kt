package info.voidev.mctest.engine.server

import info.voidev.mctest.engine.proto.EngineServiceImpl
import info.voidev.mctest.runtimesdk.proto.RuntimeService

class TestableServerSession(
    val process: Process,
    val runtime: RuntimeService,
    val engine: EngineServiceImpl,
)
