package info.voidev.mctest.runtimesdk.proto

import java.io.Serializable
import java.rmi.Remote
import java.rmi.RemoteException

interface RuntimeService : Remote, Serializable {

    @Throws(RemoteException::class)
    fun runTestMethod(testClassName: String, methodName: String, methodDescriptor: String): Monostate

    @Throws(RemoteException::class)
    fun countOnlinePlayers(): Int

    companion object {
        const val NAME = "runtime"
    }
}
