package coroutines.coroutines_deepdive._2_library.shared_state

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

private class AtomicsUserDownloader(
    private val api: NetworkService
) {
    private val users = AtomicReference(listOf<User>())

    fun downloaded(): List<User> = users.get()

    suspend fun fetchUser(id: Int) {
        val newUser = api.fetchUser(id)
        users.getAndUpdate { it + newUser }
    }
}

suspend fun main() {
    val downloader = AtomicsUserDownloader(FakeNetworkService())

    coroutineScope {
        repeat(10_000) { // 10000
            launch {
                downloader.fetchUser(it)
            }
        }
    }

    println(downloader.downloaded().size) // 969629
}
