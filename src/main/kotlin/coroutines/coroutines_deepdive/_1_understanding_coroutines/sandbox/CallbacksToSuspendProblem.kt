package coroutines.coroutines_deepdive._1_understanding_coroutines.sandbox

import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Задача: преобразовать 2 асинхронные функции с коллбеками в suspend-функции.
 */
fun main(): Unit = runBlocking {
    println("Before ${Thread.currentThread().name}")
    val userInfo = loadUserInfoSuspend(id = "123")
    val data = loadDataFromServerSuspend(userInfo = userInfo)
    println("Finish result: $data")
    println("After ${Thread.currentThread().name}")
}

suspend fun loadUserInfoSuspend(id: String) = suspendCoroutine<String> { continuation ->
    thread {
        Thread.sleep(2000)
        println("loadUserInfo finished in ${Thread.currentThread().name}")
        continuation.resume("user-$id")
    }
}

suspend fun loadDataFromServerSuspend(userInfo: String) = suspendCoroutine<String> { continuation ->
    thread {
        Thread.sleep(2000)
        println("loadDataFromServer finished in ${Thread.currentThread().name}")
        continuation.resume("Data for $userInfo")
    }
}