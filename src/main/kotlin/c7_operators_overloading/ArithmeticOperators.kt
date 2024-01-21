package c7_operators_overloading

import kotlin_in_action.operators_overloading.PointJava
import java.math.BigDecimal

/**
 * # Глава 7. Перегрузка операторов и другие соглашения.
 *
 * Некоторые особенности языка Java связаны с определёнными типами (например, Iterable можно использовать в циклах for=each, а AutoCloseable - в try-with-resources).
 * В Kotlin есть похожие особенности, но они связаны не с типами, а с функциями, названными определёнными именами (plus -> +).
 * Такой подход называется _соглашениями_: некоторые конструкции языка вызывают функции, определённые в коде приложения.
 * Это удобно для расширения Java типов с помощью extension-функций.
 *
 * ## 7.1. Перегрузка арифметических операторов.
 */

data class Point(val x: Int, val y: Int) {
    // operator - обязательное ключевое слово для перегрузки операторов.
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

fun main() {
    val p1 = Point(10, 20)
    val p2 = Point(30, 40)
    val p3 = p1 + p2 // <- компилятор вместо оператора "+" вызывает функцию plus у объекта класса Point.
    println(p3) // Point(x=40, y=60)

    /**
     * Бинарные арифметические операторы, доступные для перегрузки:
     * - a * b -> times
     * - a / b -> div
     * - a % b -> mod
     * - a + b -> plus
     * - a - b -> minus
     *
     * Тк в Java нет ключевого слова "operator", использовать функции с соответствующими именами как переопределённые:
     */
    val p1j = PointJava(10, 20)
    val p2j = PointJava(30, 40)
    val p3j = p1j + p2j
    println(p3j) // PointJava(x=40, y=60)


    /**
     *  Но наиболее распространённый шаблон при реализации соглашений для классов внешних библиотек - extension-функции.
     */
    operator fun PointJava.minus(other: PointJava): PointJava {
        return PointJava(x - other.x, y - other.y)
    }
    println(p2j - p1j) // PointJava(x=20, y=20)

    /**
     * При переопределении операторов необязательно использовать одинаковые типы операндов, а также тип результата.
     */
    operator fun PointJava.times(scale: Double): Point {
        return Point((x * scale).toInt(), (y * scale).toInt())
    }

    val point: Point = PointJava(10, 10) * 2.5
    println(point) // Point(x=25, y=25)

    /**
     * Составные операторы присваивания (+=, -=, *=, ...)
     *
     * Далее используется переопределённый бинарный оператор plus, который возвращает новое значение.
     */
    var point2 = Point(1, 2)
    point2 += Point(3, 4) // будет работать только с var
    println(point2) // Point(x=4, y=6)

    /**
     * Есть чисто функции чисто составных операторов: += -> plusAssign, -+ -> minusAssign.
     * Такие функции не возвращают новых значений, но могут изменить мутабельный объект.
     */
    data class MutablePoint(var x: Int, var y: Int)
    operator fun MutablePoint.plusAssign(other: MutablePoint) {
        x += other.x
        y += other.y
    }

    val mutablePoint = MutablePoint(1, 2)
    mutablePoint += MutablePoint(3, 4) // работает с val, тк мутабельный
    println(mutablePoint) // MutablePoint(x=4, y=6)

    /**
     *  Перегрузка унарных операторов: +a, -a, !a, ++a, --a.
     */
    operator fun BigDecimal.inc() = this + BigDecimal.ONE
    var bd = BigDecimal.ZERO
    bd++
    println(bd) // 1

}