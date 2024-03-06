package multithreading.coroutines.androidbroadcast._4_exception_handling.playground

import kotlinx.coroutines.*

private val rootScope = CoroutineScope(Dispatchers.Default)
private val handler1 =
    CoroutineExceptionHandler { coroutineContext, throwable -> println("handler 1: ${throwable.message}") }
private val handler2 =
    CoroutineExceptionHandler { coroutineContext, throwable -> println("handler 2: ${throwable.message}") }

fun main(): Unit = runBlocking {
    rootScope.launch(handler1) { // CoroutineScope перехватывает исключение только в самом родительском launch
        launch {
            launch(handler2) {
                doSomethingLong()
            }
        }

    }

    try { // функция coroutineScope как мостик между последовательным выполнением suspend-функций и асинхронным выполнением корутин
        coroutineScope {
            launch { doSomethingLong() }
        }
    } catch (e: Exception) {
        println("try-catch with coroutineScope: ${e.message}")
    }

    Thread.sleep(5000)
}

private suspend fun doSomethingLong() {
    delay(1000)
    throw RuntimeException("doSomethingLong exception")
}