package functions
/**
 * # Kotlin в действии.
 * ## 3.5 Определение и вызов функций. Работа с коллекциями.
 */

/**
 * Функция, принимающая произвольноче число аргмуентов.
 */
fun <T> printAsList(vararg elements: T){
    println(elements.joinToString())
}

/**
 * * - оператор распаковки. В Java можно было передавать аргументы из уже упакованного массива напрямую.
 * В Kotlin необходимо предворительно распаковать массив, если в функцию передаются ещё аргументы помимо массива,
 * чтобы каждое значение массива передавалось как отдельный аргумент.
 */
fun spreadOperatorUsage(array: Array<Int>) {
    println(listOf(0, array)) //[0, [Ljava.lang.Integer;@37a71e93]
    println(listOf(0, *array)) //[0, 1, 2, 3]
}

/**
 * infix call - особая форма вызова метода, когда имя метода помещается между именем целевого объекта и параметром
 * (без точки и скобок):
 */
infix fun <A, B> A.pairTo(other: B) = Pair(this, other)
val pair: Pair<Int, String> = 1 pairTo "one"


fun main() {
    printAsList(1, 2, 3)
    spreadOperatorUsage(arrayOf(1,2,3))

    /**
     * Деструктор (destructuring declarations).
     * Значениями объекта Pair можно инициализировать сразу 2 переменные, это называется мультидекларацией.
     * Это возможно, тк Pair - data class, у которого переопределены component().
     */
    val (number, name) = pair
    println("$number = $name")

    data class ValueContainer(
        val value1: String,
        val value2: Int,
        val value3: Boolean
    )
    val valueContainer = ValueContainer("string", 1, true)
    val (v1, v2, v3) = valueContainer //мультидеклорация из data class.
    println("$v1 - $v2 - $v3")

}