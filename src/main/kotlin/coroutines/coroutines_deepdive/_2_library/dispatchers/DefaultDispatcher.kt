package coroutines.coroutines_deepdive._2_library.dispatchers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Dispatcher определяет на каком потоке (пуле потоков) должна выполняться корутина.
 * Dispatchers.Default - использует пул потоков с количеством, равном количеству ядер процессора (но не меньше 2-х).
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main() = coroutineScope {
    repeat(1000) {
        launch(Dispatchers.Default.limitedParallelism(8)) { // limitedParallelism - максимальное кол-во потоков, которые могут выполняться одновременно внутри пула потоков Default!
            List(1000) { Random.nextLong() }.maxOrNull()

            println("Running on thread: ${Thread.currentThread().name}")
        }
    }
}