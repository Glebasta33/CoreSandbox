package kotlin_lang.kotlin_in_action._2_basics

/**
 * # Kotlin в действии.
 * ## 2.3 Основы Kotlin. Предоставление и обработка выбора: перечисления и конструкция when.
 */

/**
 * ## Перечисления:
 */

enum class Color(
    val r: Int, val g: Int, val b: Int
) {
    RED(255, 0, 0), ORANGE(255, 165, 0),
    YELLOW(255, 255, 0), GREEN(0, 255, 0), BLUE(0, 0, 255),
    INDIGO(75, 0, 130), VIOLET(238, 130, 238);

    fun rgb() = (r * 256 + g) * 256 + b
}

fun main() {
    println(Color.BLUE.rgb()) // 255

    /** enum - мягкое ключевое слово (soft keyword): оно имеет особое значение только перед class**/
    val enum = Color.entries.toTypedArray()
    enum.forEach { println(it) }

    /**
     * ## Оператор when:
     */

    /**
     *  Использование when с enum.
     */
    fun getMnemonic(color: Color) =
        when (color) {
            Color.RED -> "Каждый"
            Color.ORANGE -> "Охотник"
            Color.YELLOW -> "Желайет"
            Color.GREEN -> "Знать"
            Color.BLUE -> "Где"
            Color.INDIGO -> "Сидит"
            Color.VIOLET -> "Фазан"
        }

    println(getMnemonic(Color.RED)) // Каждый

    /**
     *  В одну ветку можно объеденить несколько значений, разделив их запятыми.
     */
    fun getWarmth(color: Color) = when (color) {
        Color.RED, Color.ORANGE, Color.YELLOW -> "Теплый"
        Color.GREEN -> "Нейтральный"
        Color.BLUE, Color.INDIGO, Color.VIOLET -> "Холодный"
    }

    println(getWarmth(Color.INDIGO)) // Холодный

    /**
     * when можно использовать с произвольными объектам, в отличии от switch, который требует перечиселния и константы.
     *
     * В данном примере сравниваются множества (объекты коллекции Set).
     */
    fun mix(c1: Color, c2: Color) =
        when (setOf(c1, c2)) {
            setOf(Color.RED, Color.YELLOW) -> Color.ORANGE
            setOf(Color.YELLOW, Color.BLUE) -> Color.GREEN
            setOf(Color.BLUE, Color.VIOLET) -> Color.INDIGO
            else -> throw Exception("Грязный цвет")
        }

    println(mix(Color.BLUE, Color.YELLOW)) // GREEN

    /**
     * when без аргументов (предыдущиий пример без создания Set):
     *
     * В when без аргументов условием выбора ветки может стать любое логическое выражение.
     */
    fun mixOptimized(c1: Color, c2: Color) =
        when {
            (c1 == Color.RED && c2 == Color.YELLOW) || (c1 == Color.YELLOW && c2 == Color.RED) -> Color.ORANGE
            (c1 == Color.YELLOW && c2 == Color.BLUE) || (c1 == Color.BLUE && c2 == Color.YELLOW) -> Color.GREEN
            (c1 == Color.BLUE && c2 == Color.VIOLET) || (c1 == Color.VIOLET && c2 == Color.BLUE) -> Color.INDIGO
            else -> throw Exception("Грязный цвет")
        }

    println(mixOptimized(Color.BLUE, Color.VIOLET)) // INDIGO
}