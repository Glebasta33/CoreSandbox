package multithreading.coroutines.androidbroadcast._3_coroutine_lifecycle.sandbox

import kotlinx.coroutines.*

private val exceptionHandler =
    CoroutineExceptionHandler { coroutineContext, throwable -> println("exception handler: ${throwable.message}") }
private val rootScope = CoroutineScope(CoroutineName("Root scope") + SupervisorJob() + exceptionHandler)

fun main() { //TODO: Разобраться почему вторая корутина падает несмотря на наличие SupervisorJob
    rootScope.launch(SupervisorJob()) {
        launch {
            delay(2000)
            throw RuntimeException("child crash")
        }
        launch {
            repeat(3) {
                delay(1000)
                println("scope is active: $it")
            }
        }
    }

    Thread.sleep(5000)
}