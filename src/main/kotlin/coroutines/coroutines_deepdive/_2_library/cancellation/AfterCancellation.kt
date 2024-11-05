package coroutines.coroutines_deepdive._2_library.cancellation

import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = launch {
        try {
            delay(2000)
        } finally { // После отмены прерывание и запуск других корутин не будет работать
            println("Finally")
            launch { println("Will not by printed") }

            withContext(NonCancellable) { // Но есть не отменяемый Job!
                delay(1000)
                println("Cleanup done!")
            }

            delay(1000) // выбросится CancellationException
            println("Will not by printed")
        }
    }

    job.invokeOnCompletion { exception: Throwable? ->
        println("Cleanup some external res ($exception)")
    }

    delay(1000)
    job.cancelAndJoin()
    println("Cancelled successfully")
}