package info.voidev.mctest.runtime

class SuicideThread private constructor() : Thread("Suicide") {
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
                sleep(2000)
            } catch (_: InterruptedException) {
            }

            //FIXME query ppid from OS, if different than original -> die
        }
    }

    companion object {
        private var installed = false

        @Synchronized
        fun install() {
            if (installed) return
            installed = true

//            SuicideThread().start() TODO re-add
        }
    }
}
