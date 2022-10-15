package info.voidev.mctest.engine.util

/**
 * Starts this process as a daemon,
 * meaning that it will be killed along with an ordinary shutdown of this JVM.
 *
 * For the extraordinary shutdown case,
 * the process should implement its own logic
 * to kill itself when it detects that its parent has died/changed.
 */
fun ProcessBuilder.startDaemon(): Process {
    val process = start()

    Runtime.getRuntime().addShutdownHook(Thread {
        process.destroyForcibly()
        // TODO Is this enough to guarantee destruction? Or should we just always destroyForcibly()?
    })

    return process
}
