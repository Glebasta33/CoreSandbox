package coroutines.coroutines_deepdive._3_channel_and_flow.hot_flows

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface Item {
    data object Updated : Item
}

class ItemsUpdatedInteractor {
    private val _flow = MutableSharedFlow<Item>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    val flow = _flow.asSharedFlow()

    fun notifyItemUpdated() {
        _flow.tryEmit(Item.Updated)
    }
}

class ItemsUpdatedTask(
    private val itemsUpdatedInteractor: ItemsUpdatedInteractor
) {
    fun onItemsUpdated() {
        itemsUpdatedInteractor.notifyItemUpdated()
    }
}


val itemsUpdatedInteractor = ItemsUpdatedInteractor()
val itemsUpdatedTask = ItemsUpdatedTask(itemsUpdatedInteractor)

suspend fun main(): Unit = coroutineScope {

    itemsUpdatedTask.onItemsUpdated()
    itemsUpdatedTask.onItemsUpdated()

    delay(3000)

    launch {
        itemsUpdatedInteractor.flow.collect { event ->
            println("collect 1: $event")
        }
    }

    itemsUpdatedTask.onItemsUpdated()

    delay(3000)

    launch {
        itemsUpdatedInteractor.flow.collect { event ->
            println("collect 2: $event")
        }
    }

    delay(3000)

    launch {
        itemsUpdatedInteractor.flow.collect { event ->
            println("collect 3: $event")
        }
    }
}