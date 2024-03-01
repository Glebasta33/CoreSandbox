package multithreading.coroutines.androidbroadcast._3_coroutine_lifecycle.sandbox

import kotlinx.coroutines.*

//DONE: Посмотреть, есть ли дефолтный Job у скоупа, и можно ли через него получить доступ к дочерним джобам.

private val parentScope = CoroutineScope(CoroutineName("Parent scope"))

suspend fun main() {
    val parentJob = parentScope.coroutineContext.job
    println(parentJob) // JobImpl{Active}@5e8c92f4

    val job = parentScope.launch {
        val childJob = launch {
            delay(1000)
        }

        println("childJob is child of job: ${coroutineContext.job.children.contains(childJob)}") // true
    }

    println(job) // JobImpl{Active}@5e8c92f4

    //Scope имеет дефолтный job и запущенные в нём корутины имеют дочерние job по отношению к Job скоупа.
    println("job is child of parent job: ${parentJob.children.contains(job)}") // true
}