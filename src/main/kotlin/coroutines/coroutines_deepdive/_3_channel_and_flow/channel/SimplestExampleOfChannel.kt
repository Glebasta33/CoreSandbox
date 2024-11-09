package coroutines.coroutines_deepdive._3_channel_and_flow.channel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * public interface Channel<E> : SendChannel<E>, ReceiveChannel<E>
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main(): Unit = coroutineScope {
    val channel = Channel<Int>()

    val producerJob = launch {
        repeat(5) { index ->
            delay(1000)
            println("Producing next one")
            channel.send(index * index)
        }
        channel.close() // при использовании for-each чтения в consumer - нужно закрывать канал вручную!
    }

    val consumerJob = launch {
        channel.consumeEach { element ->
            println(element)
        }
    }

    producerJob.join()
    consumerJob.join()

    // produce - coroutine builder, возвращающий ReceiveChannel. produce сам закрывает Channel по завершению корутины!
    val chnl: ReceiveChannel<Int> = produce {
        repeat(5) { index ->
            delay(1000)
            println("Producing next one")
            send(index * 100)
        }
    }

    chnl.consumeEach { element ->
        println(element)
    }
}