package info.voidev.mctest.runtimesdk.proto

import java.io.Serializable
import java.rmi.Remote
import java.rmi.RemoteException

interface EngineService : Remote, Serializable {

    /**
     * Called by R to notify E that R has bootstrapped its runtime service
     * and is about to start the Minecraft server.
     */
    @Throws(RemoteException::class)
    fun notifyBootstrapComplete()

    @Throws(RemoteException::class)
    fun notifyServerIsRunning()

    /**
     * Gets the byte array of the classfile of the class named by [name]
     * if that class exists within the classpath of the engine
     * and belongs to the testee (i.e. the plugin being tested) or the MCTest API.
     *
     * Note that we cannot use RMI's classloading mechanism
     * since these classes do not implement [Remote].
     */
    @Throws(RemoteException::class)
    fun getTesteeClass(name: String): ByteArray?

    /**
     * Checks whether the engine knows how to unmarshal the class named by [name].
     */
    @Throws(RemoteException::class)
    fun maySendClass(name: String): Boolean

    companion object {
        const val NAME = "engine"
    }
}
