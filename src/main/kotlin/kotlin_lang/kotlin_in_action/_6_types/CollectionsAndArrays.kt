package kotlin_lang.kotlin_in_action._6_types

import kotlin_in_action.types.DataParserJava
import kotlin_in_action.types.TypesJavaExample

/**
 * ## 6.3. Массивы и коллекции.
 *
 * Kotlin использует Java-коллекции и дополняет их новым функционалом, построенном на extension-функциях.
 * В отличие от Java, Kotlin разделяет все коллекции на изменяемые и неизменяемые.
 * Mutable-интерфейсы наследуют соответсвующие неизменяеые интерфейсы, добавляя к ним методы добавления, удаления элементов и т.д.
 * [Iterable <- MutableIterable, Collection <- MutableCollection, List <- MutableList]
 *
 * Любая коллекция в Kotlin - это экземпляр соответсвующей коллекции на Java. Но для каждого интерфейса Java-коллекций в Kotlin
 * существуют 2 представления: только для чтения и для чтения/записи.
 * Именно мутабельные интерфейсы непосредственно соответсвуют Java-интерфейсам в java.util.
 *
 * Kotlin->Java: код на Java сможет изменить коллекцию из Kotlin, даже если она объявлена как неизменяемая, тк в Java нет неизменяемых коллекций:
 */
fun main() {
    val list = listOf("a", "b", "c")
    println(TypesJavaExample.uppercaseAll(list)) // [A, B, C]
    println(list.first()) // A <- Java-код модифицирует неизменяемую Kotlin-коллекцию.

    /**
     * Java->Kotlin: Kotlin рассматривает коллекции, объявленные в Java, как платформенные типы с неизвестным статусом изменяемости (тут так же как и с нуллабельностью).
     * Их можно использовать и как изменяемые, и как неизменяемые. Особенно следует уделять этому внимание при переопределении Java-методов,
     * в сигнатуре которых есть коллекция.
     */
    class MutableParser : DataParserJava {
        override fun <T : Any?> parseData(input: String?, output: MutableList<T>, errors: MutableList<String?>) {

        }
    }
    class ImmutableParser : DataParserJava {
        override fun <T : Any?> parseData(input: String, output: List<T>, errors: List<String>) {
        }
    }

    /**
     * Массивы в Kotlin соответствуют массивом обёрток на Java (Array<Int> соответствует типу java.lang.Integer[]).
     * Для создания массивов примитивов без обёрток есть специальные классы для каждого примитива:
     */
    val intArray = IntArray(5) // <- массив соответствует int[] в Java
    intArray[0] = 1
    intArray[2] = 3
    println(intArray.asList()) // [1, 0, 3, 0, 0]

    // Создание массива с конструктором, принимающим лямбду:
    val squares = IntArray(5) { i -> (i + 1) * (i + 1) }
    println(squares.joinToString(", ")) // 1, 4, 9, 16, 25

    /**
     * Следующие примеры создают массивы обёрток над примитивами на Java:
     */
    val integers = arrayOf(1, 2, 3)

    val integersWithNulls = arrayOfNulls<Int>(5)
    integersWithNulls[0] = 5
    println(integersWithNulls.joinToString(", ")) // 5, null, null, null, null

    val letters = Array<String>(26) { i -> ('a' + i).toString()}
    println(letters.joinToString(", ")) // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z
}