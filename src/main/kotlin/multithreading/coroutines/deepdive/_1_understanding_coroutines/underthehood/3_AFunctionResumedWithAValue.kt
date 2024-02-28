package multithreading.coroutines.deepdive._1_understanding_coroutines.underthehood

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

/**
 * ## A function resumed with a value.
 * Представлен вызов 2-х suspend-функций, которые принимают параметры и возвращают значение.
 * ```
 * suspend fun printUser(token: String) {
 *      println("Before")
 *      val userId = getUserId(token) // suspending
 *      println("Got userId: $userId")
 *      val userName = getUserName(userId, token) // suspending
 *      println(User(userId, userName))
 *      println("After")
 * }
 * ```
 * Внутри Continuation должны храниться следующие значения:
 * - token - он нужен в состояниях 0 и 1
 * - userId - нужен в состояниях 1 и 2
 * - result: Result - задаёт то, каким образом функция будет возобновлена: Success или Failure.
 */
fun printUser(
    token: String,
    continuation: Continuation<*>
): Any {

    class PrintUserContinuation(
        val completion: Continuation<Unit>,
        val tokenProperty: String
    ) : Continuation<String> {
        override val context: CoroutineContext
            get() = completion.context

        var label = 0
        var result: Result<Any>? = null
        var userId: String? = null

        override fun resumeWith(result: Result<String>) {
            this.result = result
            val res = try {
                val r = printUser(tokenProperty, this)
                if (r == COROUTINE_SUSPENDED) return
                Result.success(r as Unit)
            } catch (e: Throwable) {
                Result.failure(e)
            }
            completion.resumeWith(res)
        }
    }

    val continuation = continuation as? PrintUserContinuation
        ?: PrintUserContinuation(
            continuation as Continuation<Unit>,
            token
        )

    var result: Result<Any>? = continuation.result
    var userId: String? = continuation.userId
    val userName: String

    if (continuation.label == 0) {
        println("Before")
        continuation.label = 1
        val res = getUserId(token, continuation)
        if (res == COROUTINE_SUSPENDED) {
            return COROUTINE_SUSPENDED
        }
        result = Result.success(res)
    }
    if (continuation.label == 1) {
        userId = result!!.getOrThrow() as String
        println("Got userId: $userId")
        continuation.label = 2 // установка следующего шага
        continuation.userId = userId // сохранения стейта в continuation
        val res = getUserName(userId, continuation) // вызов suspend-функции
        if (res == COROUTINE_SUSPENDED) { // suspension (прерывание)
            return COROUTINE_SUSPENDED
        }
        result = Result.success(res) // установка результата, если не было приостановки (not suspended)
    }
    if (continuation.label == 2) {
        userName = result!!.getOrThrow() as String
        println(User(userId as String, userName))
        println("After")
        return Unit
    }
    error("Impossible")

}


fun main() {
    printUser("token_345", EMPTY_CONTINUATION)
    Thread.sleep(5000)
}


data class User(val id: String, val name: String)

fun getUserId(token: String, continuation: Continuation<*>): Any {
    return "123"
}
fun getUserName(userId: String, continuation: Continuation<*>): Any {
    return "John"
}