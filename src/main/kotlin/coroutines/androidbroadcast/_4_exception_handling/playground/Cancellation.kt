package coroutines.androidbroadcast._4_exception_handling.playground

import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {

    val rootJob = launch(CoroutineExceptionHandler { _, throwable -> println(throwable.message) }) {
        checkActive("rootJob")
        val childJob1 = launch {
            checkActive("childJob1")
            val childJob2 = launch {
                checkActive("childJob2")
                val childJob3 = launch {
                    checkActive("childJob3")
                }
            }
            delay(500)
            childJob2.cancel()
        }
    }
    //rootJob is alive
    //childJob1 is alive

}

private suspend fun checkActive(jobName: String) {
    delay(1000)
    println("$jobName is alive")
}