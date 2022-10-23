package info.voidev.mctest.runtimesdk

object RuntimeExitCodes {
    const val USAGE = 1

    const val NO_PARENT_PROCESS = 10
    const val PARENT_HAS_DIED = 11
    const val RUNNING_TOO_LONG = 12

    const val JAVA_TOO_OLD = 20

    /**
     * A value guaranteed never to be used as an exit code by the runtime.
     */
    const val RESERVED_NO_EXIT = Int.MAX_VALUE
}
