package coroutines.coroutines_deepdive._3_channel_and_flow.select

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

/**
 * Функция select позволяет ожидать результат завершения первой корутины из нескольких.
 * Это позволяет объединять результат нескольких источников, когда нам интересен самый быстрый ответ.
 */
suspend fun requestData1(): String {
    delay(100_000)
    return "Data1"
}

suspend fun requestData2(): String {
    delay(1000)
    return "Data2"
}

private val scope = CoroutineScope(SupervisorJob())

suspend fun askMultipleForData(): String {
    val defData1 = scope.async { requestData1() }
    val defData2 = scope.async { requestData2() }

    return select {
        defData1.onAwait { it } // onAwait - коллбэк Deferred
        defData2.onAwait { it }
    }
}

suspend fun main(): Unit = coroutineScope {
    println(askMultipleForData())
}