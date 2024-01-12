package basics

import kotlin_in_action.JavaPerson

/**
 * # Kotlin в действии.
 * ## 2.2 Основы Kotlin. Классы и свойства:
 */

/**
 * Класс [JavaPerson] в Java:
 *
 * public class JavaPerson {
 *  private final String name; // поле
 *
 *  public JavaPerson(String name) { // конструктор
 *      this.name = name;
 *  }
 *
 *  public String getName() { // геттер
 *      return name;
 *  }
 * }
 */

/**
 * Класс [Person] в Kotlin:
 */
class Person(val name: String)

/**
 * - В Java данные в полях обычно хранятся с модификатором private, доступ к ним осуществляется через методы доступа:
 * геттеры и сеттеры (это обеспечивает инкапсуляцию данных внутри объекта).
 * - Свойство (property) - сочетание поля и методов доступа. Механизм свойств встроен в Kotlin.
 */

class Person2(
    val name: String, // Неизменяемое свойство: для него создаётся поле и геттер
    var isMarried: Boolean // Изменяемое свойство: для него создаётся поле, геттер и сеттер
)

fun main() {
    val javaPerson = JavaPerson("Bob") // Использоание java-класса для создания объекта в Kotlin
    println(javaPerson.name) // обращение к полю (getName под капотом).

    /**
     * Своя реализация геттера.
     */
    class Rectangle(val height: Int, val width: Int) {
        val isSquare: Boolean
            get() = height == width // геттер
    }

    val rectangle = Rectangle(25, 17)
    println(rectangle.isSquare) // false

    /**
     * Своя реализация сеттера.
     */
    class ThinPerson(val name: String, weight: Int) {
        var goodWeight: Int = weight
            set(value) { // сеттер
                if (value > 0) {
                    field = value
                } else {
                    throw RuntimeException("Нельзя установить такой маленький вес!")
                }
            }
    }

    val thinPerson = ThinPerson("John", 65)

    try {
        thinPerson.goodWeight = 0
    } catch (e: RuntimeException) {
        println(e.message)
    }



}