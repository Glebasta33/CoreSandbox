package coroutines.coroutines_deepdive._3_channel_and_flow.channel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope

/**
 * Pipelines - цепочка из Channel, в которой канал производит элемент на основе полученного элемента из другого канала.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.numbers(): ReceiveChannel<Int> = produce {
        repeat(3) { num ->
            send(num + 1)
        }
    }

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
    for (num in numbers) {
        send(num * num)
    }
}

suspend fun main(): Unit = coroutineScope {
    val numbers = numbers()
    val squared = square(numbers)
    squared.consumeEach { num ->
        println(num)
    }
}