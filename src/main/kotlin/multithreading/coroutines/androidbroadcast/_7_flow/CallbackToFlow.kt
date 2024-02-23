package multithreading.coroutines.androidbroadcast._7_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * Запрос, который выполняется асинхронно. И её результат возвращается с помощью коллбеков.
 */
interface Request<T> {

    fun execute(callback: Callback<T>) //TODO: Заробраться почему именно подход с коллбеками обеспечивает асинхронное выполнение, реализовать несколько кейсов самостоятельно.
    fun cancel()

    interface Callback<T> {
        fun onSuccess(value: T)
        fun onError(e: Exception)
    }
}

/**
 * Применение callbackFlow:
 */
fun <T> Request<T>.asFlow(): Flow<T> {
    return callbackFlow {
        execute(object : Request.Callback<T> {
            override fun onSuccess(value: T) {
                trySend(value)
                close()
            }

            override fun onError(e: Exception) {
                close(e)
            }
        })
        awaitClose { this@asFlow.cancel() }
    }
}

var singleThread: Thread? = null

val testRequest = object : Request<Int> {
    override fun execute(callback: Request.Callback<Int>) {
        singleThread = thread {
            Thread.sleep(1000)
            if (Random.nextBoolean()) {
                callback.onSuccess(200)
            } else {
                callback.onError(RuntimeException("error"))
            }
        }.apply {
            if (isInterrupted) cancel()
        }
    }
    override fun cancel() {
        singleThread?.interrupt()
    }
}


fun main() {

    // Классический подход с коллбеками
    testRequest.execute(object : Request.Callback<Int> {
        override fun onSuccess(value: Int) {
            println("Success: $value")
        }
        override fun onError(e: Exception) {
            println("Error: ${e.message}")
        }
    })

    // Конвертирование во Flow //TODO: Разобраться как запустить (где вызвать execute?).
    testRequest.asFlow()
        .catch {
            println("catch: ${it.message}")
        }
        .onEach {
            println("onEach: $it")
        }
        .launchIn(CoroutineScope(Dispatchers.Default))
}