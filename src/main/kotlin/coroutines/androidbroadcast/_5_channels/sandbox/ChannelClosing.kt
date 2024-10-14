package coroutines.androidbroadcast._5_channels.sandbox

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val channel: Channel<Int> = Channel(Channel.RENDEZVOUS)

@OptIn(DelicateCoroutinesApi::class)
fun main(): Unit = runBlocking {
    println("start")
    launch {
        channel.send(42)
    }
    println("sent")
    val result = channel.receive()
    println(result)

    println("isClosedForReceive: ${channel.isClosedForReceive}, isClosedForSend: ${channel.isClosedForSend}")
    //isClosedForReceive: false, isClosedForSend: false
    channel.close()
    println("isClosedForReceive: ${channel.isClosedForReceive}, isClosedForSend: ${channel.isClosedForSend}")
    //isClosedForReceive: true, isClosedForSend: true
    try {
        channel.receive()
    } catch (e: ClosedReceiveChannelException) {
        println("Error: ${e.message}")
    }

}