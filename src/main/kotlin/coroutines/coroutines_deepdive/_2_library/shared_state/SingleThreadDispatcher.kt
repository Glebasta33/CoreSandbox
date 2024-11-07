package coroutines.coroutines_deepdive._2_library.shared_state

import kotlinx.coroutines.*

private class SingleThreadUserDownloader(
    private val api: NetworkService
) {
    private val users = mutableListOf<User>()
    private val singleThreadDispatcher = Dispatchers.IO
        .limitedParallelism(1)

    fun downloaded(): List<User> = users.toList()

    suspend fun fetchUser(id: Int) {
        val newUser = api.fetchUser(id)
        withContext(singleThreadDispatcher) { // оборачиваем критический участок кода в однопоточный диспатчер!
            users.add(newUser)
        }

    }
}

suspend fun main() {
    val downloader = SingleThreadUserDownloader(FakeNetworkService())

    coroutineScope {
        repeat(1_000_000) {
            launch {
                downloader.fetchUser(it)
            }
        }
    }

    println(downloader.downloaded().size) // 1000000
}

