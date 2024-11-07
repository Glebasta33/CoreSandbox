package coroutines.coroutines_deepdive._2_library.custom_scope

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun CustomCoroutineScope(
    context: CoroutineContext
): CoroutineScope = ContextScope(
    if (context[Job] != null) context else context + Job()
)

private class ContextScope(
    context: CoroutineContext
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = context
    override fun toString(): String =
        "CoroutineScope(context=$coroutineContext"
}

fun main(): Unit = runBlocking {
    val scope = CustomCoroutineScope(CoroutineName("Base"))

    val job = scope.launch {
        delay(1000)
        println("Hello!")
    }

    job.join()
}