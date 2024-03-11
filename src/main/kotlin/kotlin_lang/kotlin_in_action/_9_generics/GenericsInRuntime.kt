package kotlin_lang.kotlin_in_action._9_generics

import java.security.Provider.Service
import java.util.ServiceLoader

/**
 * ## 9.2. Обобщённые типы во время выполнения: стирание и овеществление параметров типов.
 *
 * Обобщенные типы реализуются в JVM через механизм стирания типа (type erasure) - то есть типовые аргументы экземпляров
 * обобщенный классов не сохраняются во время выполнения. //TODO: Прочитать про стирание типов подробнее https://javarush.com/groups/posts/2315-stiranie-tipov
 * Чтобы типовые аргументы не стирались (или, говоря языком Kotlin, овеществлялись), функцию нужно объявить встраиваемой.
 *
 * Как и в Java, в Kotlin обобщённые типы стираются во время выполнения - то есть
 * экземпляр обобщённого класса не хранит информацию о типовых аргументах, использованных для создания этого экземпляра.
 *
 * Например, если создать список List<String> во время выполнения список будет иметь тип List, а не List<String>.
 * Информация об типовом аргументе (String) стёрлась!
 */

private fun e1() {
    val list1: List<String> = listOf("a", "b")
    println(list1.javaClass.simpleName) // ArrayList

    /**
     * В связи со стиранием типа невозможно проверить является ли объект (параметр обобщённого типа) списком строк (другим параметризованным типом).
     */
    fun <T> checkList1(value: T) {
        // ERROR: Cannot check for instance of erased type: List<String>
        //if (value is List<String>) println("list1 is List<String>")

        // Также нельзя:
        // Any() is T <- Cannot check for instance of erased type: T
    }

    /**
     * Как проверить, что значение является списком? Это можно сделать с помощью специального синтаксиса проекций.
     * Нужно указывать <*> для каждого параметра типа, присутствующего в объявлении нового параметризованного типа.
     * Аналог <?> в Java.
     */
    fun <T> checkList2(value: T) {
        // Проверяет, является ли value списком, но не говорит ничего о типе элементов списка:г
        if (value is List<*>) println("list1 is List")
    }
}

/**
 * Как сказано (выше cм. [checkList1]), обобщенные типы стираются во время выполнения.
 * В теле обобщённой функции нельзя ссылаться на типовой аргумент:
 */
//fun <T> isA(value: Any) = value is T <- ERROR: Cannot check for instance of erased type: T

/**
 * Но есть исключение: inline-функции. Типовые параметры встраиваемых функций могут овеществляться -
 * то есть во время выполнения можно ссылаться на фактические типовые аргументы.
 */
inline fun <reified T> isA(value: Any) = value is T

/**
 * Упрощенная версия объявления filterIsInstance:
 *
 * <reified T> - объявляет, что этот типовой параметр не будет стёрт во время выполнения.
 */
inline fun <reified T> Iterable<*>.filterIsInstance(): List<T> {
    val destination = mutableListOf<T>()
    for (element in this) {
        if (element is T) { //<- возможно проверить принадлежность типу
            destination.add(element)
        }
    }
    return destination
}

/**
 * Овеществление возможно только с встраиваемыми функциями потому компилятор генерирует байт-код, который
 * ссылается на конкретный класс, указанный в типовом аргументе в месте вызова inline-функции.
 *
 *     for (element in this) {
 *         if (element is String) { // Ссылается на конкретный класс
 *             destination.add(element)
 *         }
 *     }
 *
 * ## Замена ссылок на классы овеществляемыми типовыми параметрами.
 */
//устаревший вариант:
val serviceImpl = ServiceLoader.load(Service::class.java) // load(Class<S> service)

/**
 * Можно использовать функцию с овеществляемым типовым параметром:
 */
inline fun <reified T> loadService() = ServiceLoader.load(T::class.java)
val serviceImpl1 = loadService<Service>() //<- намного круче!

fun main() {
    e1()
}