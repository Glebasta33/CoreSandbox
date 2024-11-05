package coroutines.coroutines_deepdive._2_library.exception_handling

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val handler = CoroutineExceptionHandler { ctx, exception ->
        println("Caught $exception")
    }

    // CoroutineExceptionHandler не останавливает передачу исключений между дочерними корутинами
    val scope = CoroutineScope(SupervisorJob() + handler)

    scope.launch {
        delay(2000)
        throw Error("Some error")
    }

    scope.launch {
        delay(3000)
        println("Will be printed")
    }

    delay(4000)
    println("Finish")
}