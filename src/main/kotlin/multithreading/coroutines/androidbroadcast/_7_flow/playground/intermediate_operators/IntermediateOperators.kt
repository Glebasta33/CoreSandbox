package multithreading.coroutines.androidbroadcast._7_flow.playground.intermediate_operators

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform

private val coldFlow = flow { repeat(10) { emit(it) } }

suspend fun main() {
    coldFlow
        .transform { value: Int -> // transform - обобщает filter и map
            if (value % 2 == 0) {
                emit(value * 2)
            }
        }
        .collect { result ->
            println(result)
        }
}