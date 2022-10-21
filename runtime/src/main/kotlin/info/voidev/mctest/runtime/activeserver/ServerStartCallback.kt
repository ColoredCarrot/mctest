package info.voidev.mctest.runtime.activeserver

import info.voidev.mctest.runtime.EngineHolder
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

            /*
            There is a hard barrier between classes loaded from the MctestRuntimeClassLoader and any other class loader;
            attempting to conflate two versions of the same class loaded by different class loaders
            will raise unexpected errors.
            Even if two classes are exactly alike, they are different to the JVM if their class loaders differ!

            Consequently, since EngineHolder.INSTANCE was only set for the EngineHolder class
            loaded by the application class loader, it is *still null* for EngineHolder
            loaded by the runtime class loader.
            We therefore need to look up the engine service again.
             */

            val registry = LocateRegistry.getRegistry(System.getProperty("mctest.rmi.port").toInt())
            val engine = registry.lookup(EngineService.NAME) as EngineService

            EngineHolder.INSTANCE = engine

            TickableDispatcher.install(testeePluginInstance, TickableRegistry.Global)

            // We are now ready to receive "run test" requests
            engine.notifyServerIsRunning()
        }
    }

}
