package coroutines.coroutines_deepdive._2_library.exception_handling

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Когда корутина перехватывает исключение, она отменяет своего родителя,
 * который отменяет своих детей.
 * В результате - все корутины в иерархии будут отменены (если исключение не будет перехвачено).
 */
fun main(): Unit = runBlocking {
    launch {
        launch {
            delay(2000)
            throw Error("Some error")
        }

        launch {
            delay(3000)
            println("Will not be printed")
        }

        launch {
            delay(1000)
            println("Will be printed")
        }
    }

    launch {
        delay(3000)
        println("Will not be printed")
    }
}