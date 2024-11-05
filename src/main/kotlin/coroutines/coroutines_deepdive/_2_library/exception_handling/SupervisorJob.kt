package coroutines.coroutines_deepdive._2_library.exception_handling

import kotlinx.coroutines.*

/**
 * SupervisorJob - игнорирует все исключения в дочерних корутинах.
 */
fun main(): Unit = runBlocking {
    val scope = CoroutineScope(SupervisorJob()) // именно так правильно использовать SupervisorJob! либо supervisorScope

    scope.launch { // Child 1
        launch {
            delay(2000)
            throw Error("Some error")
        }

        launch {
            delay(3000)
            println("Will not be printed")
        }
    }

    scope.launch { // Child 2
        delay(3000)
        println("Will be printed")
    }

    // Частая ошибка - неправильное использование SupervisorJob!!
    launch(SupervisorJob()) { // Так у SupervisorJob будет 1 child
        launch { // Это не child SupervisorJob!
            delay(2000)
            throw Error("Some error 2")
        }

        launch { // Это не child SupervisorJob!
            delay(3000)
            println("Will not be printed")
        }
    }

    delay(4000)
}