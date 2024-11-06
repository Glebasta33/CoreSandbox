package coroutines.coroutines_deepdive._2_library.dispatchers

import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/**
 * Dispatchers.Unconfined - не меняет поток выполнения. Запускается на том потоке, в котором был вызов корутины.
 * Возобновляется на потоке, который возобновил выполнение корутины.
 */
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
suspend fun main(): Unit = withContext(newSingleThreadContext("Thread1")) {

    var continuation: Continuation<Unit>? = null

    launch(newSingleThreadContext("Thread2")) {
        delay(1000)
        continuation?.resume(Unit)
    }

    launch(Dispatchers.Unconfined) {
        println(Thread.currentThread().name) // Thread1

        suspendCancellableCoroutine {
            continuation = it // тут через continuation передаётся CoroutineContext
        }

        println(Thread.currentThread().name) // Thread2

        delay(1000) // delay использует DefaultExecutor

        println(Thread.currentThread().name) // kotlinx.coroutines.DefaultExecutor
    }
}