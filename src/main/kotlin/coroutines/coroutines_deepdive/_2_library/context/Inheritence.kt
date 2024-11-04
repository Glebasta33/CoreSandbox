package coroutines.coroutines_deepdive._2_library.context

import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

fun CoroutineScope.log(msg: String) {
    val name = coroutineContext[CoroutineName]?.name
    println("[$name] $msg")
}

fun main(): Unit = runBlocking(CoroutineName("main")) {
    log("Started")
    val v1 = async(CoroutineName("override")) {
        delay(500)
        log("Running async")
        42
    }
    launch {
        delay(1000)
        log("Running launch")
    }
    log("The answer is ${v1.await()}")
}

suspend fun printName() {
    // Каждая suspend-функция имеет доступ к coroutine context, тк под капотом у неё есть объект Continuation, который хранит поле!!!
    println(coroutineContext[CoroutineName]?.name)
}