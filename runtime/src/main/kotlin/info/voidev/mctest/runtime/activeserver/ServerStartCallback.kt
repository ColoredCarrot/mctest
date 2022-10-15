package info.voidev.mctest.runtime.activeserver

import info.voidev.mctest.runtime.activeserver.lib.tickable.TickableDispatcher
import info.voidev.mctest.runtime.activeserver.lib.tickable.TickableRegistry
import info.voidev.mctest.runtimesdk.proto.EngineService
import org.bukkit.plugin.PluginLoadOrder
import java.rmi.registry.LocateRegistry

object ServerStartCallback {

    /**
     * Called by the instrumented CraftServer at the end of `enablePlugins()`.
     */
    @Deprecated("Do not call this method directly", level = DeprecationLevel.ERROR)
    @JvmStatic
    fun afterEnablePlugins(plo: PluginLoadOrder) {
        if (plo == PluginLoadOrder.POSTWORLD) {

            val registry = LocateRegistry.getRegistry(System.getProperty("mctest.rmi.port").toInt())
            val engine = registry.lookup(EngineService.NAME) as EngineService

            TickableDispatcher.install(testeePluginInstance, TickableRegistry.Global)

            // We are now ready to receive "run test" requests
            engine.notifyServerIsRunning()
        }
    }

}
