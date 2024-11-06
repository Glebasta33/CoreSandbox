package coroutines.coroutines_deepdive._2_library.dispatchers

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Dispatchers.IO - создаёт потоки в пули при необходимости, но не больше 64.
 *
 */
fun main(): Unit = runBlocking {
    val maxThreads = 64
    val factor = 3
    val time = measureTimeMillis {
        coroutineScope {
            repeat(maxThreads * factor) {
                launch(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
            }
        }
    }
    println(time)

    /**
     * Под капотом у Dispatchers.IO и Dispatchers.Default используется один и тот же пул потоков!
     * Это значит, что если, например, корутина выполняется на Dispatchers.Default и в ней выполняется
     * withContext(Dispatchers.IO) {...} - чаще всего корутина останется на там же потоке!!
     * Поменяется только лимит на параллельное выполнение корутин!!
     */
    launch(Dispatchers.Default) {
        println(Thread.currentThread().name) // DefaultDispatcher-worker-15
        withContext(Dispatchers.IO) {
            println(Thread.currentThread().name) // DefaultDispatcher-worker-15
        }
    }
}