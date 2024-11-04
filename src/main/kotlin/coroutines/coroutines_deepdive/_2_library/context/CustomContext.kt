package coroutines.coroutines_deepdive._2_library.context

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CustomCounterContext(
    private val name: String
) : CoroutineContext.Element {

    override val key: CoroutineContext.Key<*> = Key
    private var nextNumber = 0

    fun printNext() {
        println("$name: $nextNumber")
        nextNumber++
    }

    companion object Key : CoroutineContext.Key<CustomCounterContext>
}

suspend fun printNext() {
    coroutineContext[CustomCounterContext]?.printNext()
}

suspend fun main(): Unit = withContext(CustomCounterContext("Parent")) {
    printNext()
    launch {
        printNext()
        printNext()

        launch(CustomCounterContext("Child")) {
            printNext()
            printNext()
            launch {
                printNext()
            }
        }
    }
}