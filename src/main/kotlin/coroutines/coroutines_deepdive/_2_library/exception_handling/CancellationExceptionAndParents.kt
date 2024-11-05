package coroutines.coroutines_deepdive._2_library.exception_handling

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * CancellationException не распространяется на родительские корутины!
 * CancellationException влияет только на дочерние корутины!
 */
suspend fun main(): Unit = coroutineScope {
    launch {
        launch {
            delay(2000)
            println("Will not be printed")
        }
        throw CancellationException("stop")
    }

    launch {
        delay(2000)
        println("Will be printed")
    }
}