package coroutines.coroutines_deepdive._3_channel_and_flow.builders

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow

// Mac test
suspend fun main() {
    listOf(1, 2, 3, 4, 5)
        // or setOf(...), or sequenceOf(...)
        .asFlow()
        .collect { print(it) }

    val function: suspend () -> String = suspend {
        delay(1000)
        "UserName"
    }

    function.asFlow()
        .collect{ println(it) }

    ::getUserName
        .asFlow()
        .collect{ println(it) }
}

suspend fun getUserName(): String {
    delay(1000)
    return "UserName"
}