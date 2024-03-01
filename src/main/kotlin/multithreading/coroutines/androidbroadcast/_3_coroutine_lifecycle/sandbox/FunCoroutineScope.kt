package multithreading.coroutines.androidbroadcast._3_coroutine_lifecycle.sandbox

import kotlinx.coroutines.*

/**
 * coroutineScope { } может быть полезна для запуска новой корутины внутри suspend-функции, чтобы выполнить задачу параллельно.
 */
private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->  println(throwable.message) }
private val parentScope = CoroutineScope(CoroutineName("Parent scope") + coroutineExceptionHandler)

suspend fun main() {
    parentScope.launch {
        someSuspendFun()
    }
//    delay(3000)
//    parentScope.cancel() //Остановка родительского scope приведёт к остановке внутреннего
    parentScope.launch {
        delay(6000)
        println("ParentScope child")
    }
    Thread.sleep(7000)
}

suspend fun someSuspendFun() {
    delay(1000)
    println("someSuspendFun 1")
    coroutineScope { // coroutineScope выполнится как suspend-функция, но корутины внутри могут работать параллельно.
        launch(Dispatchers.IO) {
            repeat(3) {
                delay(1000)
                println("1 coroutineScope $it")
            }
        }
        launch(Dispatchers.IO) {
            repeat(3) {
                delay(1000)
                if (it == 2) error("fail") // Если происходит креш внутри, то он пробрасывается наверх родительскому scope
                println("2 coroutineScope $it")
            }
        }
    }
    delay(1000)
    println("someSuspendFun 2")
}