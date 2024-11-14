package coroutines.coroutines_deepdive._3_channel_and_flow.hot_flows

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * ```
 * public interface MutableSharedFlow<T> : SharedFlow<T>, FlowCollector<T> {
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main(): Unit = coroutineScope {
    val mutableSharedFlow = MutableSharedFlow<String>(
        replay = 2 // Размер кэша, количество последних значений, которые будут удержаны и считаны.
    )

    mutableSharedFlow.emit("Msg-1")
    mutableSharedFlow.emit("Msg-2")
    mutableSharedFlow.emit("Msg-3")

    println(mutableSharedFlow.replayCache)

    launch {
        mutableSharedFlow.collect {
            println("#1 received $it")
        }
    }

    launch {
        mutableSharedFlow.collect {
            println("#2 received $it")
        }
    }

    delay(2000)
    mutableSharedFlow.resetReplayCache()
    println(mutableSharedFlow.replayCache)

}