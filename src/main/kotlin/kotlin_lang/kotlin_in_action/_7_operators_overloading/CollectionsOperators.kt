package kotlin_lang.kotlin_in_action._7_operators_overloading

import java.time.LocalDate

/**
 * ## 7.3. Соглашения для коллекций и диапазонов.
 */
fun main() {
    /**
     * К элементам коллекции можно обращаться по индексу как в массивах: a[b] (оператор индекса).
     * Это возможно, потому что оператор индекса компилируется в вызов функций get и set.
     */
    operator fun Point.get(index: Int): Int {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
        }
    }

    val p = Point(10, 20)
    println(p[1]) // 20 <- обращение как к элементу массива.

    operator fun MutablePoint.set(index: Int, value: Int) {
        when (index) {
            0 -> x = value
            1 -> y = value
            else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
        }
    }

    val mp = MutablePoint(10, 20)
    mp[1] = 42
    println(mp) // MutablePoint(x=10, y=42)

    /**
     * Оператор "in" используется для проверки вхождения объекта в коллекцию.
     * "in" -> contains().
     */
    data class Rectangle(val upperLeft: Point, val lowerRight: Point)

    operator fun Rectangle.contains(p: Point): Boolean {
        return p.x in upperLeft.x until lowerRight.x &&
                p.y in upperLeft.y until lowerRight.y
    }

    val rect = Rectangle(Point(0,0), Point(100, 100))
    println(Point(50, 70) in rect) // true
    println(Point(101, 105) in rect) // false

    /**
     * Оператор для создания диапазонов ".." вызывает функцию rangeTo, которая возвращает закрытый диапазон.
     * rangeTo расширяет интерфейс [Comparable] и возвращает [ClosedRange]
     */
    val now: LocalDate = LocalDate.now()
    val vacation: ClosedRange<LocalDate> = now..now.plusDays(10)
    println(now.plusDays(5) in vacation) // true

    /**
     * Циклы в Kotlin используют тот же оператор in, который применяется для проверки принадлежности диапазону.
     * "for (x in list) {...}" транслируется в "list.iterator()", который вызывает hasNext и Next.
     * В Kotlin iterator() - это соглашение, и его можно определить как extension-функцию.
     * Например, operator fun CharSequence.iterator(): CharIterator - объясняет почему можно пробегаться по строке в цикле:
     */
    for (c in "abc") print("$c, ") // a, b, c,

    /**
     * Можно написать свою реализацию итератора для прохождения в цикле по своему диапазону объектов.
     */
    // for (d in vacation) <- error: For-loop range must have an 'iterator()' method
    operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
        object : Iterator<LocalDate> {
            var current = start

            override fun hasNext(): Boolean = current <= endInclusive

            override fun next(): LocalDate = current.apply {
                current = plusDays(1)
            }
        }

    val nextMonthFirstDay = LocalDate.now().plusMonths(1).minusDays(LocalDate.now().dayOfMonth.toLong() - 1)
    val monthCountDown = LocalDate.now()..nextMonthFirstDay

    for (day in monthCountDown) print("$day, ") // 2024-01-21, 2024-01-22, 2024-01-23, 2024-01-24, 2024-01-25, 2024-01-26, 2024-01-27, 2024-01-28, 2024-01-29, 2024-01-30, 2024-01-31, 2024-02-01,

}