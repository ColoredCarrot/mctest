package info.voidev.mctest.runtime

import info.voidev.mctest.runtimesdk.RuntimeExitCodes
import kotlin.system.exitProcess

class SuicideThread private constructor(private val deadline: Long) : Thread("Suicide") {

    init {
        isDaemon = true

        try {
            priority = MIN_PRIORITY
        } catch (_: SecurityException) {
            // We don't care if setting the priority fails
        }
    }

    override fun run() {
        while (true) {
            try {
                sleep(10_000)
            } catch (_: InterruptedException) {
            }

            //FIXME query ppid from OS, if different than original -> die

            if (System.currentTimeMillis() > deadline) {
                System.err.println("The process has been running for too long; the parent has probably died by now. Exiting")
                exitProcess(RuntimeExitCodes.RUNNING_TOO_LONG)
            }
        }
    }

    companion object {
        private var installed = false

        @Synchronized
        fun install(maxAliveMs: Long) {
            if (installed) return
            installed = true

            var deadline = System.currentTimeMillis() + maxAliveMs
            if (deadline < 0) { // overflow
                deadline = Long.MAX_VALUE
            }

            SuicideThread(deadline).start()
        }
    }
}
