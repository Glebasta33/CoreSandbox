package c7_operators_overloading

/**
 * ## 7.4. Мультидекларации и функции component.
 *
 * Мультидекларация - объявление нескольких переменных в скобках. Основана на принципе соглашений.
 * Для инициализации каждой переменной вызываются функции с именем componentN, где N - номер позиции переменной в объявлении.
 * Для data-классов функции component сгенерируются автоматически. Для не data-классов можно их объявить внутри класса.
 */
class CustomPoint(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2() = y
}

fun main() {
    val p = CustomPoint(10, 20)
    val (c1, c2) = p
    println(c1) // 10

    /**
     * Мультидекларация может быть удобным средством получения сразу нескольких значений из функции.
     */

    fun getTimedPoint(x: Int) = CustomPoint(x * x, x * x)
    val (x, y) = getTimedPoint(10)
    println(y) // 100

    /**
     * Мультидеклорации можно использовать в циклах, при прохождении по коллекциям.
     * Например, [Map.Entry] имеют функции расширения component1() и component2().
     */
    val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
    for ((key, value) in map) {
        println("$key -> $value")
    }
}