package multithreading.coroutines.deepdive._1_understanding_coroutines.sandbox

import kotlin.concurrent.thread

fun main() {
    println("Before ${Thread.currentThread().name}")
    loadDataFromServer { data ->
        println(data)
    }
    println("After ${Thread.currentThread().name}")
}

fun loadDataFromServer(callback: (String) -> Unit) {
    thread {
        Thread.sleep(2000)
        callback.invoke("Data ${Thread.currentThread().name}")
    }
}