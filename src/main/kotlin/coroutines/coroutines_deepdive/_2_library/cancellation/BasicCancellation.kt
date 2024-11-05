package coroutines.coroutines_deepdive._2_library.cancellation

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main(): Unit = coroutineScope {
    val job = launch {
        repeat(1000) { i ->

            try {
                delay(200) // в точке прерывания - генерится CancellationException!
            } catch (e: CancellationException) {
                println(e)
                throw e
            }

            println("Step $i")
        }
    }

    delay(1100)
    job.cancel()
    job.join()
    println("Cancelled successfully")
}