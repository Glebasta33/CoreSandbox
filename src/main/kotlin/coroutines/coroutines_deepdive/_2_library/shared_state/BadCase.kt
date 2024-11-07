package coroutines.coroutines_deepdive._2_library.shared_state

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private class UserDownloader(
    private val api: NetworkService
) {
    private val users = mutableListOf<User>()

    fun downloaded(): List<User> = users.toList()

    suspend fun fetchUser(id: Int) {
        val newUser = api.fetchUser(id)
        users.add(newUser)
    }
}

suspend fun main() {
    val downloader = UserDownloader(FakeNetworkService())

    coroutineScope {
        repeat(1_000_000) {
            launch {
                downloader.fetchUser(it)
            }
        }
    }

    println(downloader.downloaded().size) // 969629
}

class FakeNetworkService : NetworkService {
    override suspend fun fetchUser(id: Int): User {
        delay(2)
        return User("User-$id")
    }
}

interface NetworkService {
    suspend fun fetchUser(id: Int): User
}

data class User(val name: String)