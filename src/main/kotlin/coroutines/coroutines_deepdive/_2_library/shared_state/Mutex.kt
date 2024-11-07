package coroutines.coroutines_deepdive._2_library.shared_state

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Mutex создаёт синхронизированную секцию, которая может выполняться только одной корутиной за раз:
 * - первая корутина входит в секцию без приостановки.
 * - затем вторая корутина приостанавливается при вызове withLock{} (lock()), пока первая не завершит выполнение секции.
 * - последующие корутины - приостанавливаются и кладутся в очередь.
 */
private class MutexUserDownloader(
    private val api: NetworkService
) {
    private val users = mutableListOf<User>()
    private val mutex = Mutex()

    fun downloaded(): List<User> = users.toList()

    suspend fun fetchUser(id: Int) {
        val newUser = api.fetchUser(id)

//        mutex.lock()
//        try {
//            users.add(newUser)
//        } finally {
//            mutex.unlock() // правильно вызывать unlock в finally, на случай исключения
//        }

//      withLock использует эту же идиому под капотом
        mutex.withLock {
            users.add(newUser)
        }

    }
}

suspend fun main() {
    val downloader = MutexUserDownloader(FakeNetworkService())

    coroutineScope {
        repeat(1_000_000) { // 1000000
            launch {
                downloader.fetchUser(it)
            }
        }
    }

    println(downloader.downloaded().size) // 969629

    // Важная особенность Mutex: корутина не может войти занятый собой же Mutex повторно (не поддерживает Reentrant).
    // В данном случае будет Deadlock:
    val mutex = Mutex()
    println("Started")
    mutex.withLock {
        mutex.withLock {
            println("Will never be printed!")
        }
    }
    println("Will never be printed!")
}

