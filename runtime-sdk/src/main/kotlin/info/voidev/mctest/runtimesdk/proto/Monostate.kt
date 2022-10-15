package info.voidev.mctest.runtimesdk.proto

import java.io.Serializable

/**
 * Used as the return type for void RMI methods
 * to force RMI to block until method completion.
 */
//TODO Check whether this is actually needed
object Monostate : Serializable
