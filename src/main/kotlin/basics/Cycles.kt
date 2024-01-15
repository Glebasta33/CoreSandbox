package basics

import java.util.TreeMap

/**
 * # Kotlin в действии.
 * ## 2.4 Основы Kotlin. Итерации: циклы "while" и "for".
 */

/**
 * Цикл for существует в Java только в форме аналога for-each из Java
 * (цикл всегда проходится по некоторому диапозону значений).
 *
 * Диапазон - интервал между двумя значениями.
 *
 * В Kotlin нельзя создать цикл как в Java:
 *         for (int i = 0; i < 100; i++) {
 *             System.out.println(i);
 *         }
 */

fun main() {
    for (i in 0..10) println(i) //включающий диапазон
    for (i in 0 until 10) println(i) //закрытый диапазон
    for (i in 10 downTo 0 step 2) println(i)

    //итерация по Map
    val binaryMap: MutableMap<Char, String> = TreeMap<Char, String>()
    for (c in 'A'..'F') { //диапазон букв
        val binary = Integer.toBinaryString(c.toInt())
        binaryMap[c] = binary
    }

    for ((letter, binary) in binaryMap) {
        println("$letter = $binary")
    }
}