package coroutines.coroutines_deepdive._3_channel_and_flow.select

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select

/**
 * select может быть использована с Channel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun CoroutineScope.produceString(
    s: String,
    time: Long
) = produce {
    while (true) {
        delay(time)
        send(s)
    }
}

fun main() = runBlocking {
    val fooChannel = produceString("foo", 200)
    val barChannel = produceString("BAR", 500)

    repeat(7) {
        select {
            fooChannel.onReceive {
                println("From fooChannel: $it")
            }

            barChannel.onReceive {
                println("From barChannel: $it")
            }
        }
    }

    coroutineContext.cancelChildren()
}