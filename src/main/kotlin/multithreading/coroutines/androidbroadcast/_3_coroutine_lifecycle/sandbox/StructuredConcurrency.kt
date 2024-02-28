package multithreading.coroutines.androidbroadcast._3_coroutine_lifecycle.sandbox

import kotlinx.coroutines.*

private val rootScope = CoroutineScope(CoroutineName("Root scope"))

fun main(): Unit = runBlocking {

    rootScope.launch(CoroutineName("parent coroutine")) {
        launch { }
        launch { }
        launch {
            launch { }
            launch { }
            launch {
                launch { }
                launch { }
                launch { }
            }
        }
    }

    printChildren(rootScope.coroutineContext.job)

}

private fun printChildren(job: Job) {
    println("$job")
    job.children.forEach { childJob ->
        printChildren(childJob)
    }
}
//JobImpl{Active}@1ef7fe8e
//StandaloneCoroutine{Completing}@64c64813
//StandaloneCoroutine{Completing}@3ecf72fd
//StandaloneCoroutine{Completed}@483bf400
//StandaloneCoroutine{Active}@21a06946
//StandaloneCoroutine{Active}@77f03bb1