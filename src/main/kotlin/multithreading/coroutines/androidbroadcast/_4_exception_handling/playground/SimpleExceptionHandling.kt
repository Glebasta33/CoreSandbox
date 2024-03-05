package multithreading.coroutines.androidbroadcast._4_exception_handling.playground

import kotlinx.coroutines.*

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val scope = CoroutineScope(newSingleThreadContext("root scope"))
private val handler1 =
    CoroutineExceptionHandler { coroutineContext, throwable -> println("handler 1: ${throwable.message}") }
private val handler2 =
    CoroutineExceptionHandler { coroutineContext, throwable -> println("handler 2: ${throwable.message}") }

fun main(): Unit = runBlocking {
    try {
        doSomethingLong()
    } catch (e: Exception) {
        println(e.message)
    }


    /**
     * Исключение всегда будет передаваться родительскому Job в иерархии,
     * и только на вершине иерархии CoroutineExceptionHandler будет способен его перехватить.
     */
    scope.launch(handler1) {
        try {
            launch(handler2) {
                doSomethingLong()
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
    //handler 1: doSomethingLong exception


    supervisorScope {
        val deferred: Deferred<String> = async {
            getResultWithException()
        }

        try {
            deferred.await()
        } catch (e: Exception) {
            println(e.message)
        }
    }



    Thread.sleep(5000)
}

private suspend fun doSomethingLong() {
    delay(1000)
    throw RuntimeException("doSomethingLong exception")
}

private suspend fun getResultWithException(throwException: Boolean = true): String {
    delay(1000)
    return if (throwException) {
        throw RuntimeException("getResult exception")
    } else {
        "Result"
    }
}