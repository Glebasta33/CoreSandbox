package multithreading.coroutines.deepdive._1_understanding_coroutines.underthehood

import kotlinx.coroutines.delay

/**
 * ## The call stack.
 * Когда когда функция "а" вызывает функцию "b", виртуальная машина должна где-то хранить состояние функции "а",
 * а также место, в котором должно возобновиться её выполнение после завершения функции "b".
 * Всё это хранится в структуре, называемой "call stack"(стек вызова).
 * Но когда мы в корутинах приостанавливаем (suspend) выполнение, мы освобождаем поток и как результат - очищаем call stack.
 * Таким образом call stack не может быть использован для возобновления выполнения функции.
 * Вместо этого Continuations служат стеком вызова.
 * Каждый сontinuation сохраняет состояние функции в месте приостановки (label), локальные переменные и параметры,
 * а также ссылку на сontinuation функции, которая вызвала текущую функцию.
 * Один сontinuation ссылается на другой, который ссылается на третий. В результате сontinuation - это многослойная структура (как огромный лук).
 *
 * Пример ("а" вызывает "b", которая вызывает "c"):
 */
suspend fun a() { //TODO: Написать реализацию suspend-функций с 3-мя уровнями вложенности
    val user = readUser()
    b()
    b()
    b()
    println(user)
}
suspend fun b() {
    for (i in 1..10) {
        c(i)
    }
}
suspend fun c(i: Int) {
    delay(i * 100L)
    println("Tick")
}

/**
 * Continuation для данных вызовов функций может быть представлен следующим образом:
 * ```
 * CContinuation(
 *  i = 4,
 *  label = 1,
 *  completion = BContinuation(
 *      i = 4,
 *      label = 1,
 *      completion = AContinuation(
 *          label = 2,
 *          user = User@1234,
 *          completion = ...
 *      )
 *  )
 * )
 * ```
 *
 * Когда continuation возобновляется, каждое continuation сначала вызывает свою функцию [1].
 * Затем continuation возобновляет continuation родительской функции [2].
 * Этот процесс продолжается пока не будет достигнута верхушка стека вызовов.
 * ```
 * override fun resumeWith(result: Result<String>) {
 *      this.result = result
 *      val res = try {
 *          val r = printUser(token, this) [1]
 *          if (r == COROUTINE_SUSPENDED) return
 *           Result.success(r as Unit)
 *      } catch (e: Throwable) {
 *          Result.failure(e)
 *      }
 *      completion.resumeWith(res) [2]
 * }
 *
 * ```
 */



private fun readUser() = "User"
