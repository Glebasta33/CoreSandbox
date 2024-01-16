package c2_basics



/**
 * # Kotlin в действии.
 * ## 2.1 Основы Kotlin. Переменные и функции
 */

/**
 * ## Функции:
 */

fun main() {
    println("Hello World!")
    println(maxExpressionBody(1, 2))
    println(maxBlockBody(2, 1))
}

/**
 * Пример функции с телом-блоком (block body).
 */
fun maxExpressionBody(a: Int, b: Int): Int {
    /**
     * В Kotlin if - это выражение (возвращает значение), а не инструкция (не имеет собственного значения).
     * В Kotlin большинство управляющих структур - выражения (кроме циклов).
     */
    return if (a > b) a else b
}

/**
 * Пример функции с телом-выражением (expression body).
 * Тип возвщаяемого значения не указан явно, тк компилятор Kotlin способен распознавать тип выражения и использовать
 * его в качестве типа возвщаяемого значения функции (механизм называется выведением типа - type inference).
 */
fun maxBlockBody(a: Int, b: Int) = if (a > b) a else b

/**
 * ## Переменные:
 */

/**
 * 2 типа переменных:
 * - val - неизменяемая (immutable) ссылка. Нельзя присвоить другое значения. Соответсвует final переменным в Java.
 * - var - изменяемая (mutable) ссылка. Можно присвоить другое значение.
 */
val question = "Question ...?"
var answer = 42

fun useValAndVar() {
    // question = "Q..." - ошибка: нельзя изменить значение val
    answer = 24
}