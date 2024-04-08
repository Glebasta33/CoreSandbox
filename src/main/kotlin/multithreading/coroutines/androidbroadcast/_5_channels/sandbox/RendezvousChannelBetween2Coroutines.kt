package multithreading.coroutines.androidbroadcast._5_channels.sandbox

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val channel: Channel<Int> = Channel(Channel.RENDEZVOUS)

/**
 * И принимающая, и отправляющая корутина будет приостановлена до тех пор пока канал полностью не отработает.
 * При Channel.RENDEZVOUS каждый метод send() должен иметь "пару" - received(), и наоборот.
 */
fun main(): Unit = runBlocking {
    launch {
        println("c1 started")
        channel.send(1)
        println("value: 1 sent from c1")
        channel.send(2)
        println("value: 2 sent from c1")
    }

    launch {
        println("c2 started")
        delay(2000)
        val receivedValue = channel.receive()
        println("value: $receivedValue received in c2")
        val receivedValue2 = channel.receive()
        println("value: $receivedValue2 received in c2")
    }
}