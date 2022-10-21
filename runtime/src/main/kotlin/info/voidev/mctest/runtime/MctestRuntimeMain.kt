package info.voidev.mctest.runtime

import info.voidev.mctest.runtime.classloading.MctestBootstrapClassLoader
import info.voidev.mctest.runtime.classloading.MctestRuntimeClassLoader
import info.voidev.mctest.runtime.proto.RuntimeServiceImpl
import info.voidev.mctest.runtimesdk.proto.EngineService
import info.voidev.mctest.runtimesdk.proto.RuntimeService
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.system.exitProcess

class MctestRuntimeMain

fun main(args: Array<String>) {
    // Commit suicide as soon as our parent dies
    setUpDieWithParent()

    if (args.size != 3) {
        System.err.println("Usage: <serverJarPath> <rmiPort> <testeePluginName>")
        exitProcess(1)
    }

    System.err.println("Bootstrapping MCTest Runtime")

    val serverJar = Path(args[0])
    val rmiPort = args[1].toInt()
    val testeePluginName = args[2]

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

private fun setUpDieWithParent() {
    val parentProcess: ProcessHandle? = ProcessHandle.current().parent().orElse(null)
    if (parentProcess == null) {
        System.err.println("Must be created with a parent process")
        exitProcess(1)
    }

    parentProcess.onExit().handleAsync { _, _ ->
        System.err.println("Parent has died, exiting")
        // TODO: Figure out if it's worth it to Bukkit.shutdown() normally
        exitProcess(0)
    }

    SuicideThread.install(TimeUnit.MINUTES.toMillis(60))
}
