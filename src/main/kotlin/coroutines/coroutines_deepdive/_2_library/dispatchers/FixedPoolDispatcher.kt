package coroutines.coroutines_deepdive._2_library.dispatchers

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

private const val NUMBER_OF_THREADS = 10

fun main(): Unit = runBlocking {
    val dispatcher = Executors
        .newFixedThreadPool(NUMBER_OF_THREADS)
        .asCoroutineDispatcher() // Можно создать свой dispatcher, гибко настроив Executors

    repeat(10) {
        launch(dispatcher) {
            Thread.sleep(1000)
        }
    }

    dispatcher.close() // Важно вручную закрывать кастомный пул!
}