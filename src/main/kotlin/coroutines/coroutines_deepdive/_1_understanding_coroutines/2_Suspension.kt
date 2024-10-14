package coroutines.coroutines_deepdive._1_understanding_coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * # How does suspension work?
 * (... from the user’s point of view.)
 *
 * Прерываемость suspend-функции - это главная особенность корутин, на которой строятся все остальные
 * концепции в Kotlin Coroutines.
 *
 * ## Аналогия корутин.
 * Прерывание корутины означает - приостановку в середине. Это можно сравнить с чек-поинтом в игре:
 * когда ты выключаешь игру, и ты, и компьютер могут заниматься другими вещами. Но когда ты захочешь
 * продолжить (continue) игру, игра возобновляется с сохранённого чек-поинта.
 * Когда корутины прерываются, они возвращают Continuation(Продолжение). Как в игре: мы можем использовать это,
 * чтобы продолжить с точки, где мы остановились.
 *
 * Это поведение сильно отличается от потоков, которые не могут быть сохранены, но блокируются полностью.
 *
 * Пример прерывания: код приостановится после println("Before")
 * но не будет завершён.
 */
suspend fun e1() {
    println("Before")
    suspendCoroutine<Unit> { } // suspend-функция
    println("After") // не напечатается
}

/**
 * Чтобы продолжить выполнение примера e1, нужно возобновить (continue) корутину.
 * Функция suspendCoroutine принимает лямбду с параметром типа Continuation.
 * ```
 * fun <T> suspendCoroutine(crossinline block: (Continuation<T>) -> Unit): T
 * ```
 *
 */
suspend fun e2() {
    println("Before")
    suspendCoroutine<Unit> { continuation ->
        println("Before too")
        continuation.resume(Unit) // продолжить выполнение
    }
    println("After") // напечатается, тк есть вызов resume внутри suspendCoroutine
}

/**
 * Функция resume использует стандартную ф-ию интерфейса Continuation:
 * ```
 * inline fun <T> Continuation<T>.resume(value: T): Unit =
 *     resumeWith(Result.success(value))
 * ```
 * Интерфейс, представляющий продолжение(Continuation) после точки приостановки, возвращающий значение типа T.
 * ```
 * public interface Continuation<in T> {
 *     public val context: CoroutineContext
 *     public fun resumeWith(result: Result<T>)
 * }
 * ```
 *
 * Мы также можем запустить внутри suspendCoroutine другой поток и заблокировать его.
 */
suspend fun e3() {
    println("Before")
    suspendCoroutine<Unit> { continuation ->
        thread {
            println("Suspended")
            Thread.sleep(2000)
            continuation.resume(Unit)
            println("Resumed")
        }

    }
    println("After")
}
//Before
//Suspended
// (2-seconds delay...)
//After
//Resumed
/**
 *
 * !!! (См. Callbacks.kt - обычный коллбэк в другом потоке не возобновляет выполнение в месте вызова,
 * а исполняется независимо, асинхронно).
 */

/**
 * Создавать для приостановки каждый раз новый поток - слишком дорого. Лучше использовать выделенный поток:
 */
private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true }
}

suspend fun e4() {
    println("Before")
    suspendCoroutine<Unit> { continuation ->
        executor.schedule({
            continuation.resume(Unit)
        }, 1, TimeUnit.SECONDS)
    }
    println("After")
}

/**
 * Функционал приостановки выполнения на определённое время можно вынести в отдельную функцию - delay.
 * (Точно таким же образом реализован delay в корутинах).
 */
suspend fun delay(timeMillis: Long): Unit =
    suspendCoroutine { continuation ->
        executor.schedule({
            continuation.resume(Unit)
        }, timeMillis, TimeUnit.MILLISECONDS)
    }

suspend fun e5() {
    println("Before")
    delay(1000)
    println("After")
}

/**
 * ## Возобновление со значением.
 * Continuation<in T> - дженерик, и мы можем указать какой тип может быть возвращён этим Continuation.
 * (Продолжая аналогию игры и чек-поинта, это как возвращение в игру, после получения инфы по прохождению).
 */
suspend fun e6() {
    val i: Int = suspendCoroutine<Int> { cont ->
        cont.resume(42)
    }
    println(i) // 42
    val str: String = suspendCoroutine<String> { cont ->
        cont.resume("Some text")
    }
    println(str) // Some text
}

/**
 * ## Возобновление с ошибкой.
 * Когда вместо данных мы получаем ошибку, исключение пробрасывается из точки, где корутина была приостановлена.
 */
suspend fun e7() {
    try {
        suspendCoroutine<Unit> { cont ->
            cont.resumeWithException(Exception())
        }
    } catch (e: Exception) {
        println("Caught!")
    }
}

/**
 * ## Приостанавливается корутина, не функция.
 * Приостанавливается корутина, а не сама suspend функция.
 * suspend-функции - это не корутины, а просто функции, которые могут приостанавливать корутины.
 */
var continuation: Continuation<Unit>? = null // так не стоит делать из-за потенциальных утечек памяти
suspend fun suspendAndSetContinuation() {
    suspendCoroutine<Unit> { cont ->
        continuation = cont
    }
}

suspend fun e8() = coroutineScope{
    println("Before")
    launch {
        delay(1000)
        continuation?.resume(Unit)
    }
    suspendAndSetContinuation()
    println("After")
}
// Before
// (1-second delay)
// After

suspend fun main() {
    e3()
}