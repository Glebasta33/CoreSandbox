package functions

/**
 * # Kotlin в действии.
 * ## 3.1 Определение и вызов функций. Создание коллекций.
 */

fun main() {
    val set = hashSetOf(1, 3, 5)
    val list = arrayListOf(1, 3, 5)
    val map = hashMapOf(1 to "one", 3 to "three", 5 to "five")

    println(set.javaClass) //class java.util.HashSet
    println(list.javaClass) //class java.util.ArrayList
    println(map.javaClass) //class java.util.HashMap
    /**
     * Kotlin использует стандартные классы коллекций из Java.
     * Но функционал коллекций в Kotlin значительно расширен.
     */
    println(set.max())
}

