package coroutines.androidbroadcast._1_coroutine

import kotlinx.coroutines.*
import kotlin.random.Random

fun main(): Unit = runBlocking {
    repeat(5) { i ->
        val result = calculation1("$i")
        println(result)
    }
}

suspend fun calculation1(name: String): String {
    delay(Random.nextLong(5000))
    return "Done on ${Thread.currentThread().name}. $name"
}