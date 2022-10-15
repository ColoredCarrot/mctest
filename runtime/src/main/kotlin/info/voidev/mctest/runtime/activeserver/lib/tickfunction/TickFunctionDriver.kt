package info.voidev.mctest.runtime.activeserver.lib.tickfunction

import info.voidev.mctest.api.TickFunctionScope
import info.voidev.mctest.runtime.activeserver.lib.tickable.Tickable
import info.voidev.mctest.runtime.activeserver.lib.tickable.TickableRegistry
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

private class TickFunctionDriver<R> private constructor(
    private val func: TickFunction<R>,
    private val tickableRegistry: TickableRegistry,
) : Continuation<R>, TickFunctionScope, Tickable {

    // null if in reset state (either done or uninitialized)
    private var nextCont: Continuation<Unit>? = null

    var lastResultOrNull: Result<R>? = null
        private set

    val lastResult get() = lastResultOrNull ?: error("No last result")

    fun reset() {
        // Cannot be in init{} block because `this` escapes
        nextCont = func.createCoroutineUnintercepted(this, this)
    }

    override suspend fun yieldTick() {
        suspendCoroutineUninterceptedOrReturn<Unit> { cont ->
            nextCont = cont
            COROUTINE_SUSPENDED
        }
    }

    override fun tick() {
        nextCont?.resume(Unit)
    }

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resumeWith(result: Result<R>) {
        nextCont = null
        lastResultOrNull = result
        tickableRegistry.unregister(this)
    }

    companion object {
        operator fun <R> invoke(func: TickFunction<R>, tickableRegistry: TickableRegistry) =
            TickFunctionDriver(func, tickableRegistry).also { it.reset() }
    }
}

fun <R> launchTickFunction(tickableRegistry: TickableRegistry = TickableRegistry.Global, f: TickFunction<R>): CompletableFuture<R> {
    val future = CompletableFuture<R>()

    val driver = TickFunctionDriver({
        try {
            future.complete(f())
        } catch (ex: Throwable) {
            future.completeExceptionally(ex)
        }
    }, tickableRegistry)

    // Start ticking the driver
    tickableRegistry.register(driver)

    return future
}

///**
// * Runs the given [TickFunction] [f] on the primary thread
// * and synchronously blocks until its completion.
// */
//fun <R> runBlocking(f: TickFunction<R>): R {
//    require(!Bukkit.isPrimaryThread()) { "Cannot run a tick function from the primary thread in blocking mode (this would deadlock)" }
//
//    val latch = CountDownLatch(1)
//
//    val driver = TickFunctionDriver {
//        try {
//            f()
//        } finally {
//            latch.countDown()
//        }
//    }
//
//    // Start ticking the driver
//    TickableRegistry.register(driver)
//
//    // Wait until the tick function counts down the latch
//    latch.await()
//
//    return driver.lastResult.getOrThrow()
//}
