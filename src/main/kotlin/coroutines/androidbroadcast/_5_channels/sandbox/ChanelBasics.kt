package coroutines.androidbroadcast._5_channels.sandbox

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val channel: Channel<Int> = Channel(Channel.RENDEZVOUS)

fun main1(): Unit = runBlocking {
    println("start")
    channel.send(42)
    println("sent")
    val result = channel.receive()
    println(result)
}
//start
//... И функция никогда не закончит выполнение, тк будет приостановлена на моменте вызова send.
//Нужно обеспечить, чтобы receive вызывался в другой корутине.

fun main(): Unit = runBlocking {
    println("start")
    launch {
        channel.send(42)
    }
    println("sent")
    val result = channel.receive()
    println(result)
}
//start
//sent
//42