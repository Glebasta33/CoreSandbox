package coroutines.coroutines_deepdive._1_understanding_coroutines.underthehood.sandbox

import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
    println("Before")

//    suspendCoroutine<Unit> {  } // код приостановится на этом месте навсегда.

    suspendCoroutine<Unit> { continuation ->
        thread {
            println("Suspended")
            Thread.sleep(2000)
            continuation.resume(Unit) // код возобновляется,
        }
    }

    println("After")
}