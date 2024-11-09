package coroutines.coroutines_deepdive._3_channel_and_flow.channel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

/**
 * Тип Channel определяется размером capacity (количеством элементов в буфере):
 * - Unlimited - буфер не ограничен.
 * - Buffered - ограниченный буфер (64 по дефолту).
 * - Rendezvous (default) - буфер равен 0 (передача данных осуществляется от 1 корутине к другой).
 * - Conflated - буфер равен 1: каждый новый элемент заменяет предыдущий (consumer потребит только 1 последний элемент.
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main(): Unit = coroutineScope {
    val channel = produce(capacity = Channel.CONFLATED) {
        repeat(5) { index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }
    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}