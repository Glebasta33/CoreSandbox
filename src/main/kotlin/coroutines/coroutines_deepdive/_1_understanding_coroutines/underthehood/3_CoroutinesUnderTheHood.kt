package coroutines.coroutines_deepdive._1_understanding_coroutines.underthehood

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

/**
 * # Coroutines under the hood.
 * Основные тезисы:
 * - suspend-функции - это state-машины с определённым состоянием в начале функции и после каждого вызова suspend-функций.
 * - Как состояние, так и локальные данные хранятся в объекте Continuation.
 * - Continuation функции декорирует Continuation вызывающей его функции. В результате чего Continuation представляет
 * стек вызовов, который используется для приостановки и возобновления suspend-функций.
 *
 * В Kotlin Coroutines __объекты Continuation передаются от функции к функции как аргумент (Continuation-passing style)__.
 * __Параметр Continuation всегда добавляется последним в каждую suspend-функцию под капотом.__
 * ```
 * suspend fun getUser(): User?
 * suspend fun setUser(user: User)
 * suspend fun checkAvailability(flight: Flight): Boolean
 *
 * // under the hood is
 * fun getUser(continuation: Continuation<*>): Any?
 * fun setUser(user: User, continuation: Continuation<*>): Any
 * fun checkAvailability(
 *      flight: Flight,
 *      continuation: Continuation<*>
 * ): Any
 * ```
 * __Каждая suspend-функция преобразуется в обычную функцию с параметром Continuation.__
 * __Также каждой suspend-функции добавляется тип возвращаемого значения Any или Any?.__
 * Почему именно Any? Если suspend-функция не вернёт значения, она возвращает специальный объект-маркер COROUTINE_SUSPENDED : Any.
 *
 * ## A very simple function
 * Допустим есть следующая простая suspend-функция.
 * ```
 * suspend fun myFunction() { //TODO: Реализовать с самостоятельно реализацию функции с 3-мя delay.
 *     println("Before")
 *     delay(1000)
 *     println("After")
 * }
 *```
 * Далее идёт примерная реализация suspend-функции в том виде, как она примерно бы выглядела в виде декомпилированного байт-кода.
 */
fun myFunction(continuation: Continuation<Unit>): Any {
    /**
     * Функции myFunction нужен своя реализация continuation(которая является обёрткой над continuation из параметра), чтобы запомнить свой стейт.
     * (в реально байт-коде поле continuation имеет тип Object $continuation).
     */
    class MyFunctionContinuation(
        val completion: Continuation<Unit>
    ) : Continuation<Unit> {
        override val context: CoroutineContext
            get() = completion.context

        //в реализации Continuation внутри myFunction есть поля label и result
        var label = 0
        var result: Result<Any>? = null

        //resumeWith - это по сути коллбек, который вызывается изнутри continuation для продолжения выполнения кода после прерывания
        override fun resumeWith(result: Result<Unit>) {
            //! вызов myFunction(this) - внутрь передаётся текущий экземпляр Continuation
            this.result = result
            val res: Result<Unit> = try {
                val r = myFunction(this) //<- рефлексия
                if (r == COROUTINE_SUSPENDED) return
                Result.success(r as Unit)
            } catch (e: Throwable) {
                Result.failure(e)
            }
            completion.resumeWith(res)//!! В конце происходит вызов внешней Continuation
        }
    }

    // Внутри функции есть переменная $continuation, которая инициализируется некоторой реализацией, принимающей в себя параметр Continuation: $continuation = new ContinuationImpl(var0)
    val continuation = continuation as? MyFunctionContinuation ?: MyFunctionContinuation(continuation)

    //Используется switch-case по $continuation.label, внутри которого так же меняется значение label.
    if (continuation.label == 0) {
        println("Before")
        continuation.label = 1 //Увеличение label
        // Вызов delay и передача в него текущего continuation (внутри delay - вызов continuation.resumeWith!)
        if (delay(1000, continuation) == COROUTINE_SUSPENDED) {
            return COROUTINE_SUSPENDED //Выход из функции myFunction
        }
    }
    if (continuation.label == 1) {
        println("After")
        return Unit
    }
    error("Impossible")
}

private val executor = Executors
    .newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

fun delay(timeMillis: Long, continuation: Continuation<Unit>): Any {
    executor.schedule({
        continuation.resume(Unit)
    }, timeMillis, TimeUnit.MILLISECONDS)
    return COROUTINE_SUSPENDED
}

val EMPTY_CONTINUATION = object : Continuation<Unit> {
    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        // Это корневая корутина, в данном случае ничего не нужно возвращать
    }
}

fun main() {
    myFunction(EMPTY_CONTINUATION)
    Thread.sleep(2000)
}

val COROUTINE_SUSPENDED = Any()