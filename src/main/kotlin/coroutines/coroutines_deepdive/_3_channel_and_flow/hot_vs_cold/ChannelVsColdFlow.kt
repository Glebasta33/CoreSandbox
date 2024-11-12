package coroutines.coroutines_deepdive._3_channel_and_flow.hot_vs_cold

import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow

private fun CoroutineScope.makeChannel() = produce {
    println("Channel started")
    for (i in 1..3) {
        delay(1000)
        send(i)
    }
}

private fun makeFlow() = flow {
    println("Flow started")
    for (i in 1..3) {
        delay(1000)
        emit(i)
    }
}

suspend fun main() = coroutineScope {
    val channel = makeChannel() // Hot. Данные эмитятся независимо от потребителей
    delay(1000)
    println("Calling channel...")
    for (value in channel) {
        println(value)
    }
    println("Consuming channel again")
    for (value in channel) {
        println(value)
    }

    println("\n-------------------- \n")
    delay(3000)

    val flow = makeFlow() // Cold. Данные эмитятся по каждому требованию потребителей!
    delay(1000)
    println("Calling flow")
    flow.collect { println(it) } // вызов терминального оператора
    println("Consuming flow again")
    flow.collect { println(it) }
}