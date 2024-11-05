package coroutines.coroutines_deepdive._2_library.cancellation

import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.coroutines.resume

suspend fun retrofitCall() = suspendCancellableCoroutine { continuation: CancellableContinuation<String> ->
    thread {
        Thread.sleep(2000) // network call
        continuation.resume("Result")
    }

    continuation.invokeOnCancellation {
        println("Thread cleanup")
    }
}

fun main(): Unit = runBlocking {
    val deferred = async { retrofitCall() }
    delay(1000)
    deferred.cancelAndJoin()
}