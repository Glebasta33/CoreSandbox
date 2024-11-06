package coroutines.coroutines_deepdive._2_library.dispatchers

import kotlinx.coroutines.*
import java.util.concurrent.Executors

/**
 * При использовании Dispatcher`ов нужно учитывать проблему доступа к общему состоянию,
 * тк под капотом используются разные потоки!
 *
 */
private var i = 0
private var y = 0

suspend fun main(): Unit = coroutineScope {
    repeat(10_000) {
        launch(Dispatchers.IO) {
            i++
        }
    }
    delay(1000)
    println(i) // 9890 - Race condition!


    // Одно из решений - использование single thread dispatcher:
    val singleThreadDispatcher = Executors.newSingleThreadExecutor()
        .asCoroutineDispatcher()
    //or Dispatchers.Default.limitedParallelism(1)
    //Но блокирующие операции будут выполняться последовательно!

    repeat(10_000) {
        launch(singleThreadDispatcher) {
            y++
        }
    }
    delay(1000)
    println(y)
    singleThreadDispatcher.close()
}