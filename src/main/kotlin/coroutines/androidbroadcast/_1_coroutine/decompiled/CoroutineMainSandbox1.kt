package coroutines.androidbroadcast._1_coroutine.decompiled

import kotlinx.coroutines.*
import kotlin.random.Random

fun main(): Unit = runBlocking {
    val deferred = async {
        val name = "Param for suspend function"
        val calculationResult = withContext(Dispatchers.Default) {
            calculation(name)
        }
        calculationResult
    }

    val deferredResult = deferred.await()
    println(deferredResult)
}

suspend fun calculation(name: String): String {
    delay(Random.nextLong(5000))
    return "Done on ${Thread.currentThread().name}. $name"
}