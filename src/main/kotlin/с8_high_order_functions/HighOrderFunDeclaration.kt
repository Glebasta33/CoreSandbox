package с8_high_order_functions

/**
 * # 8. Функции высшего порядка: лямбда-выражения как параметры.
 * ## 8.1. Объявление функции высшего порядка.
 * Функции высшего порядка - это функции, которые принимают другие функции в аргументах и/или возвращают их.
 * Например, функция filter принимает аргумент с функцией-предикатом и, соответственно, является функцией высшего порядка:
 *
 *      list.filter { x > 0 }
 *
 * ## Функциональные типы.
 * Лямбда-выражение можно хранить в переменной следующего типа:
 */
val sum = { x: Int, y: Int -> x + y }
val action = { println(42) }

// Явное объявление типов переменной:
val sumExpl: (Int, Int) -> Int = { x, y -> x + y }
val actionExpl: () -> Unit = { println(42) }

// Функциональный тип может быть нуллабельным, как и любой другой тип:
var funOrNull: ((Int, Int) -> Int)? = null

/**
 * Определение функционального типа может включать именованные параметры (в лямбде - не обязательно указывать те же имена,
 * имена параметров улучшают читаемость и могут использоваться при автокомплите IDE).
 */
fun performRequest(
    url: String,
    callback: (code: Int, content: String) -> Unit
) {
    println("url call: $url")
    Thread.sleep(1000)
    callback.invoke(200, "{content}")
}

fun main() {
    performRequest("http://kotl.in") { code, content -> println("$code - $content") }

    /**
     * Определение простой функции высшего порядка:
     */
    fun twoAndThree(operator: (Int, Int) -> Int) { // <- объявление параметра функционального типа
        val result = operator.invoke(2, 3) // <- вызов параметра функционального типа (как вызов обычной функции)
        println("The result is $result")
    }
    twoAndThree { a, b -> a + b } // The result is 5
    twoAndThree { a, b -> a * b } // The result is 6

    /**
     * Определение аналога функции filter (только для String).
     *
     * @param predicate - функция, которая получает параметр из одного символа и возвращает результат Boolean.
     */
    fun String.filter(predicate: (Char) -> Boolean): String {
        val sb = StringBuilder()
        for (index in indices) {
            val element = get(index)
            if (predicate(element)) sb.append(element)
        }
        return sb.toString()
    }

    println("ab1c23".filter { c: Char -> c in 'a'..'z' }) // abc

    /**
     * Практика: определение аналога функции findFirstOrNull (только для List).
     */
    fun <T> List<T>.findFirstOrNull(predicate: (T) -> Boolean): T? {
        forEach { element ->
            if (predicate(element)) return element
        }
        return null
    }

    listOf("A", "B", "C", "D").findFirstOrNull { it == "D" }.let { println(it) } // D
    listOf("A", "B", "C", "D").findFirstOrNull { it == "X" }.let { println(it) } // null

    /**
     * Под капотом функциональные типы - это реализации функциональных интерфейсов (с именем FunctionN<T, ...n >).
     * Каждый такой интерфейс определяет один метод invoke, который содержит тело лямбда-выражения.
     *
     *     fun processTheAnswer(f: (Int) -> Int) {
     *         println(f(42))
     *     }
     *
     *     Подобная функция на Java будет вызываться следующим образом:
     *     processTheAnswer(
     *                 new Function1<Integer, Integer>() {
     *                     @Override
     *                     public Integer invoke(Integer number) {
     *                         return number + 1;
     *                     }
     *                 }
     *         );
     *
     *     С Java 8 возможно вызывать лямбды:
     *     processTheAnswer(number -> number + 1);
     *
     * Для функциональных типов можно задавать значение по умолчанию.
     */
    fun <T> List<T>.findFirstOrNullWithDefault(
        predicate: (T) -> Boolean = { it == it.toString() } // <- установка значения по умолчанию для функционального типа.
    ): T? {
        forEach { element ->
            if (predicate(element)) return element
        }
        return null
    }
    listOf("A", "B", "C", "D").findFirstOrNullWithDefault().let { println(it) } // A

    /**
     * ## Функциональный тип возвращаемого значения:
     * Это может быть полезно, например, для реализации функции, которая возвращает различную логику, в зависимости от условий.
     */
    class Order(val itemCount: Int)

    fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double { // <- Функция возвращает функциональный  тип
        return when (delivery) {
            Delivery.STANDARD -> { order -> order.itemCount * 1.2 }
            Delivery.EXPEDITED -> { order -> order.itemCount * 2.1 + 6 }
        }
    }

    val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
    println("Shipping costs ${calculator(Order(3))}") // Shipping costs 12.3
}

enum class Delivery { STANDARD, EXPEDITED }

