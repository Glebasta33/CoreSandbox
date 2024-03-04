package multithreading.coroutines.androidbroadcast._3_coroutine_lifecycle.sandbox

import kotlinx.coroutines.*

private val rootScope = CoroutineScope(CoroutineName("Root scope") + Dispatchers.Default + CoroutineExceptionHandler { _, _ ->  })

/**
 * Все элементы (влючая даже CoroutineExceptionHandler) CoroutinesContext наследуются дочерними корутиными и могут быть переопределены.
 * Но Job создаётся новый с привязкой к родительскому Job.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    rootScope.launch {
        println("CoroutineContext 1: ${this.coroutineContext}, parentJob: ${this.coroutineContext.job.parent}")
        launch(CoroutineName("Child")) {
            println("CoroutineContext 2: ${this.coroutineContext}, parentJob: ${this.coroutineContext.job.parent}")
            launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->  }) {
                println("CoroutineContext 3: ${this.coroutineContext}, parentJob: ${this.coroutineContext.job.parent}")
            }
        }
    }
    Thread.sleep(1000)
    //CoroutineContext 1: [CoroutineName(Root scope), CoroutineExceptionHandler$1@29568250, StandaloneCoroutine{Active}@5096cef7, Dispatchers.Default], parentJob: JobImpl{Active}@5a95861d
    //CoroutineContext 2: [CoroutineExceptionHandler$1@29568250, CoroutineName(Child), StandaloneCoroutine{Active}@2aa207d0, Dispatchers.Default], parentJob: StandaloneCoroutine{Active}@5096cef7
    //CoroutineContext 3: [CoroutineExceptionHandler$1@51df76bd, CoroutineName(Child), StandaloneCoroutine{Active}@7e6c1805, Dispatchers.IO], parentJob: StandaloneCoroutine{Completing}@2aa207d0
}