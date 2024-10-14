package coroutines.androidbroadcast._7_flow.playground.intermediate_operators

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
fun main(): Unit = runBlocking {

    val containersFlow = flow {
        repeat(2) {
            emit(
                flowOf("$it-one", "$it-two")
            )
        }
    }

    containersFlow
        .flattenConcat() // Преобразует Flow<Flow> в Flow.
        .collect {
            println(it)
        }

    containersFlow
        .flatMapConcat { innerFlow ->// Преобразует Flow<Flow> в Flow + модифицирует элементы
            innerFlow.map { it.uppercase() }
        }
        .collect {
            println(it)
        }
}