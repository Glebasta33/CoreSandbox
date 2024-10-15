package coroutines.androidbroadcast._7_flow.playground.intermediate_operators

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import coroutines.coroutines_deepdive._1_understanding_coroutines.delay

/**
 * debounce отфильтровывает все элементы, которые эмиттятся чаще, чем установленный таймаут.
 * То есть заэмитится тот элемент, после которого последует пауза длиной в таймаут.
 */
@OptIn(FlowPreview::class)
private val flow = flow {
    repeat(100) {
        val delay = if (it == 50 || it == 70) 1000L else 100L
        emit(it)
        delay(delay)
    }
}
    .debounce(500)

fun main(): Unit = runBlocking {
    flow
        .collect {
            println(it)
        }
    //50, 70, 99
}