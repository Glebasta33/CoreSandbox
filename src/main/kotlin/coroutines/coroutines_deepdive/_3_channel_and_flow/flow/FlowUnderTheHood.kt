package coroutines.coroutines_deepdive._3_channel_and_flow.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

/**
 * Flow - это просто определение того, каким образом вычислять значения по требованию
 * (так же как и Sequence, но асинхронно, с поддержкой функциональности Coroutines).
 *
 * Это похоже на suspend-лямбду:
 */
suspend fun main() {
    val f: suspend ((String) -> Unit) -> Unit = { emit ->
        emit("A")
        delay(1000)
        emit("B")
        delay(1000)
        emit("C")
    }
    f { print(it) }
    f { print(it) }

    /**
     * Интерфейс FlowCollector уже реализует подобную функциональность:
     * ```
     * public fun interface FlowCollector<in T> {
     *     public suspend fun emit(value: T)
     * }
     * ```
     */
    val f1: suspend FlowCollector<String>.() -> Unit = {
        emit("A")
        delay(1000)
        emit("B")
        delay(1000)
        emit("C")
    }
    f1 { print(it) }
    f1 { print(it) }

    /**
     * Вместо передачи лямбды можно использовать объект Flow.
     * Flow работает так:
     * collect - вызывает выражение emit в flow-билдере,
     * а выражение emit вызывает лямбду, указанную в collect!
     */
    val builder: suspend FlowCollector<String>.() -> Unit = {
        emit("A")
        emit("B")
        emit("C")
    }
    val flow: Flow<String> = object : Flow<String> {
        override suspend fun collect(collector: FlowCollector<String>) {
            collector.builder()
        }
    }
    flow.collect { print(it) }
    flow.collect { print(it) }

    /**
     * Это и есть flow-билдер:
     */
    val flow1: Flow<String> = flow {
        emit("A")
        emit("B")
        emit("C")
    }
    flow1.collect { print(it) }
    flow1.collect { print(it) }
}