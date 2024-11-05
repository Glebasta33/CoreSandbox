package coroutines.coroutines_deepdive._2_library.scopes

import kotlinx.coroutines.*

/**
 * coroutineScope - это suspend-функция, которая создаёт CoroutineScope.
 * Scope наследует контекст, но переопределяет Job.
 * См. также supervisorScope, withContext, withTimeout - всё это suspend-функции, создающие скоуп.
 * Это не то же самое, что coroutine builder (async, launch,..) !!
 */
suspend fun longTask() = coroutineScope {
    launch {
        delay(1000)
        println("Async task 1 finished in [${coroutineContext[CoroutineName]?.name}]") // Scope наследует контекст
    }
    launch {
        delay(2000)
        println("Async task 2 finished in [${coroutineContext[CoroutineName]?.name}]") // Scope наследует контекст
    }
}

fun main() = runBlocking(CoroutineName("Parent")) {
    println("Before suspend fun")
    longTask()
    println("After suspend fun")
}