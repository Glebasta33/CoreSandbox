package coroutines.androidbroadcast._3_coroutine_lifecycle.sandbox

import kotlinx.coroutines.*

private val exceptionHandler =
    CoroutineExceptionHandler { coroutineContext, throwable -> println("exception handler: ${throwable.message}") }
private val rootScope = CoroutineScope(CoroutineName("Root scope") + SupervisorJob() + exceptionHandler)

/**
 * Логика SupervisorJob (ошибка отменяет работу других дочерних Job) относится только к прямым детям.
 * К потомкам детей это не относится.
 */
fun main() {
    rootScope.launch {
        launch {
            delay(2000)
            throw RuntimeException("child crash")
        }
        launch {
            repeat(3) {
                delay(1000)
                println("sub-child is active: $it")
            }
        }
    }

    rootScope.launch {
        repeat(3) {
            delay(1000)
            println("child is active: $it")
        }
    }

    Thread.sleep(5000)
}