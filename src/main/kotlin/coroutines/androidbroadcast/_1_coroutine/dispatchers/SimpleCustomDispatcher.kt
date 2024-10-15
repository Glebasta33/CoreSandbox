package coroutines.androidbroadcast._1_coroutine.dispatchers

import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

fun main(): Unit = runBlocking {
    val exec: ExecutorService = Executors.newFixedThreadPool(2)

    /**
     * Под капотом создаётся [ExecutorCoroutineDispatcherImpl], в котором переопределяется метод dispatch:
     *
     * ```
     *     override fun dispatch(context: CoroutineContext, block: Runnable) {
     *         try {
     *             executor.execute(wrapTask(block))
     *         } catch (e: RejectedExecutionException) {
     *          ...
     *         }
     *     }
     * ```
     */
    val twoThreadsDispatcher: ExecutorCoroutineDispatcher = exec.asCoroutineDispatcher()

    repeat(10) { i ->
        launch(twoThreadsDispatcher) { awaitAndPrint(i) }
    }
}

suspend fun awaitAndPrint(i: Int) {
    delay(Random.nextLong(0, 500))
    println("Coroutine $i finished on ${Thread.currentThread().name}")
}