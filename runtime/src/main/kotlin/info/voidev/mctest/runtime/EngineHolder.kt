package info.voidev.mctest.runtime

import info.voidev.mctest.runtimesdk.proto.EngineService

object EngineHolder {

    @JvmStatic
    @Volatile
    lateinit var INSTANCE: EngineService

}
