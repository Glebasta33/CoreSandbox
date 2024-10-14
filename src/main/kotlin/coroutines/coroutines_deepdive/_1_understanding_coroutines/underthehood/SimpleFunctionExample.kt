package coroutines.coroutines_deepdive._1_understanding_coroutines.underthehood
import kotlinx.coroutines.delay

/**
 * Функция для просмотра байт-кода
 */
suspend fun myFunction() {
    println("Before")
    delay(1000)
    println("After")
}