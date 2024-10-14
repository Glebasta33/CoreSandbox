package coroutines.coroutines_deepdive._1_understanding_coroutines.underthehood

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

/**
 * ## A function with a state.
 * Если функция содержит какой-то стейт (например, локальную переменную или параметр), который
 * должен быть восстановлен после прерывания, этот стейт должен быть сохранён в Continuation данной функции.
 * ```
 * suspend fun myFunctionWithState() {
 *      println("Before")
 *      var counter = 0 // внутренний стейт функции
 *      delay(1000) // suspending
 *      counter++
 *      println("Counter: $counter")
 *      println("After")
 * }
 * ```
 * counter - стейт, который нужен в двух состояниях (когда label равен 0 и 1), следовательно, он должен быть
 * сохранён в Continuation.
 *
 * Как данная функция (примерно) будет выглядеть под капотом:
 */
fun myFunctionWithState(continuation: Continuation<Unit>): Any {
    class MyFunctionWithStateContinuation(val completion: Continuation<Unit>) : Continuation<Unit> {
        override val context: CoroutineContext
            get() = completion.context
        var result: Result<Unit>? = null
        var label = 0
        var counter = 0

        override fun resumeWith(result: Result<Unit>) {
            this.result = result
            val res: Result<Unit> = try {
                val r = myFunctionWithState(this)
                if (r == COROUTINE_SUSPENDED) return
                Result.success(r as Unit)
            } catch (e: Throwable) {
                Result.failure(e)
            }
            completion.resumeWith(res)
        }
    }

    val continuation = continuation as? MyFunctionWithStateContinuation ?: MyFunctionWithStateContinuation(continuation)

    var counter = continuation.counter

    if (continuation.label == 0) {
        println("Before")
        counter = 0
        continuation.counter = counter //изменение стейта внутри Continuation
        continuation.label = 1
        if (delay(1000, continuation) == COROUTINE_SUSPENDED) {
            return COROUTINE_SUSPENDED
        }
    }

    if (continuation.label == 1) {
        counter = (counter as Int) + 1
        println("Counter: $counter")
        println("After")
        return Unit
    }
    error("Impossible")
}

fun main() {
    myFunctionWithState(EMPTY_CONTINUATION)
    Thread.sleep(2000)
}
