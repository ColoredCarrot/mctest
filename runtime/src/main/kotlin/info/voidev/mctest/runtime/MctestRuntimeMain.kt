package info.voidev.mctest.runtime

import info.voidev.mctest.runtime.classloading.MctestBootstrapClassLoader
import info.voidev.mctest.runtime.classloading.MctestRuntimeClassLoader
import info.voidev.mctest.runtime.proto.RuntimeServiceImpl
import info.voidev.mctest.runtimesdk.RuntimeExitCodes
import info.voidev.mctest.runtimesdk.proto.EngineService
import info.voidev.mctest.runtimesdk.proto.MctestConfig
import info.voidev.mctest.runtimesdk.proto.RuntimeService
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.system.exitProcess

class MctestRuntimeMain

fun main(args: Array<String>) {
    if (args.size != 3) {
        System.err.println("Usage: <serverJarPath> <rmiPort> <testeePluginName>")
        exitProcess(RuntimeExitCodes.USAGE)
    }

    // We need at least Java 17
    if (Runtime.version() < Runtime.Version.parse("17")) {
        System.err.println("Java version is too old (need at least 17): ${Runtime.version()}")
        exitProcess(RuntimeExitCodes.JAVA_TOO_OLD)
    }

    System.err.println("Bootstrapping MCTest Runtime")

    val serverJar = Path(args[0])
    val rmiPort = args[1].toInt()
    val testeePluginName = args[2]

    // A lightweight way to share data across the class loader barrier is via System.get/setProperty.
    // See comment in ServerStartCallback.afterEnablePlugins for an explanation of this barrier.
    System.setProperty("mctest.rmi.port", rmiPort.toString())
    System.setProperty("mctest.testee.plugin.name", testeePluginName)

    System.err.println("> Running in: ${Path(".").absolutePathString()}")
    System.err.println("> Using server JAR: $serverJar")
    System.err.println("> Using RMI port: $rmiPort")
    System.err.println("> Plugin under test: $testeePluginName")
    System.err.println("Server output is available through STDOUT.")

    val service = RuntimeServiceImpl()

    val serviceStub = UnicastRemoteObject.exportObject(service, 0) as RuntimeService

    val registry = LocateRegistry.getRegistry(rmiPort)
    registry.bind(RuntimeService.NAME, serviceStub)

    val engine = registry.lookup(EngineService.NAME) as EngineService
    EngineHolder.INSTANCE = engine
    val config = engine.getConfiguration()

    // Commit suicide as soon as our parent dies
    setUpDieWithParent(config)

    val applicationClassLoader = MctestRuntimeMain::class.java.classLoader
    val classLoader = MctestBootstrapClassLoader(serverJar, applicationClassLoader)

    MctestRuntimeClassLoader.engineService = engine

    engine.notifyBootstrapComplete()

    // Note:
    // We load the Bukkit Bootstrap Main class with the MctestBootstrapClassLoader.
    // That classloader instruments Bukkit Bootstrap Main to load the actual Bukkit Main class
    // via MctestRuntimeClassLoader
    // This must be our bottom-most class loader (i.e. all our other class loaders must be parents),
    // since only the class loader that *actually loaded* the Main class will be registered with that class.

    try {
        Class.forName("org.bukkit.craftbukkit.bootstrap.Main", true, classLoader)
            .getDeclaredMethod("main", Array<String>::class.java)
            .invoke(null, arrayOf("--nogui"))
    } catch (ex: Throwable) {
        ex.printStackTrace(System.err)
    }
}

private fun setUpDieWithParent(config: MctestConfig) {
    val parentProcess: ProcessHandle? = ProcessHandle.current().parent().orElse(null)
    if (parentProcess == null) {
        System.err.println("Must be created with a parent process")
        exitProcess(RuntimeExitCodes.NO_PARENT_PROCESS)
    }

    parentProcess.onExit().handleAsync { _, _ ->
        System.err.println("Parent has died, exiting")
        // TODO: Figure out if it's worth it to Bukkit.shutdown() normally
        exitProcess(RuntimeExitCodes.PARENT_HAS_DIED)
    }

    SuicideThread.install(config.runtimeGlobalTimeoutMs)
}
