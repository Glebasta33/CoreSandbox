package coroutines.coroutines_deepdive._2_library.shared_state

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * Семафор используется не столько для синхронизации доступа к общему состоянию,
 * сколько для ограничения количества конкурентных операций.
 */
suspend fun main() = coroutineScope {
    val semaphore = Semaphore(2)

    repeat(10) {
        launch {
            semaphore.withPermit { // использует acquire и release под капотом
                delay(1000)
                print(it)
            }
        }
    }
}