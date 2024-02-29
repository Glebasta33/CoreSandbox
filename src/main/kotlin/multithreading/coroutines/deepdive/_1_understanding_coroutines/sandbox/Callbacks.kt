package multithreading.coroutines.deepdive._1_understanding_coroutines.sandbox

import kotlin.concurrent.thread

/**
 * Коллбек - это по сути объект анонимного класса.
 * В main - находится реализация коллбеков, которые передаются в методы loadUserInfo и loadDataFromServer.
 * Внутри методов loadUserInfo и loadDataFromServer происходит вызов этих коллбеков (объектов анонимного класса), которые живут в main, из фоновых потоков.
 */
fun main() {
    println("Before ${Thread.currentThread().name}")
    loadUserInfo(id = "123") { userInfo: String ->
        loadDataFromServer(userInfo = userInfo) { data ->
            println(data)
        }
    }
    println("After ${Thread.currentThread().name}")
}

fun loadUserInfo(id: String, callback: (userInfo: String) -> Unit) {
    thread {
        Thread.sleep(2000)
        callback.invoke("user-$id")
        println("loadUserInfo finished in ${Thread.currentThread().name}")
    }
}

fun loadDataFromServer(userInfo: String, callback: (String) -> Unit) {
    thread {
        Thread.sleep(2000)
        callback.invoke("Data for $userInfo")
        println("loadDataFromServer finished in ${Thread.currentThread().name}")
    }
}

