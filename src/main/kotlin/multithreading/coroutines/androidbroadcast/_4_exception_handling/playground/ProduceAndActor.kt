package multithreading.coroutines.androidbroadcast._4_exception_handling.playground

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

@OptIn(ObsoleteCoroutinesApi::class, ExperimentalCoroutinesApi::class)
fun main(): Unit = runBlocking {
    val actorChannel: SendChannel<String> = actor {
        for (i in this.channel) println(i)
    }
    actorChannel.send("Sent using actor")

    val produceChannel: ReceiveChannel<String> = produce {
        send("Sent from produce")
    }
    for (i in produceChannel) println(i)

    Thread.sleep(3000)
}