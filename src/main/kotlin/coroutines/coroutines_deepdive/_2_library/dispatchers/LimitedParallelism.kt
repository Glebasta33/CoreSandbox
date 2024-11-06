package coroutines.coroutines_deepdive._2_library.dispatchers

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * limitedParallelism у Dispatchers.IO создаёт отдельный и независимы пул потоков
 * (точнее подмножество потоков в едином неограниченном пуле потоков).
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main(): Unit = coroutineScope {
    launch {
        printCoroutinesTime(Dispatchers.IO) // Dispatchers.IO took: 2019
    }

    launch {
        val dispatcher = Dispatchers.IO
            .limitedParallelism(100) // LimitedDispatcher@19d7ce1d took: 1035
        printCoroutinesTime(dispatcher)
    }
}

suspend fun printCoroutinesTime(
    dispatcher: CoroutineDispatcher
) {
    val time = measureTimeMillis {
        coroutineScope {
            repeat(100) {
                launch(dispatcher) {
                    Thread.sleep(1000)
                }
            }
        }
    }
    println("$dispatcher took: $time")
}