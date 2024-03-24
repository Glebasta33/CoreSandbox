package kotlin_lang.kotlin_in_action._8_high_order_functions

import kotlin_lang.kotlin_in_action._5_lambdas.Person

/**
 * ## 8.3. Порядок выполнения функций высшего порядка.
 *
 * Инструкции "return" в лямбда-выражениях: выход из вмещающей функции.
 */
fun main() {
    val people = listOf(Person("Alice", 29), Person("Bob", 31))

    fun lookForAlice(people: List<Person>) {
        for (person in people) {
            if (person.name == "Alice") {
                println("Found")
                return // <- функция lookForAlice вернёт управление вызывающему коду
            }
        }
        println("Alice is not found")
    }

    lookForAlice(people) // Found

    /**
     * Но можно ли использовать этот код внутри лямбд? Вернёт ли "return" управление вызывающему коду?
     *
     * В inline-ФУНКЦИЯХ ключевое слово "return" при вызове внутри лямбда-параметра приводит к выходу из самой функции, а не из лямбды (нелокальный возврат).
     *
     * В данном примере "return" выходит из lookForAliceLambda, а не из лямбды forEach.
     *
     * Выход из внешней функции выполняется только, когда функция, принимающая лямбда-выражение, является встраиваемой.
     * (в скомпилированном виде вмещающая функция будет непосредственно содержать в своём коде "return").
     * Использование "return" в лямбда-выражениях, передаваемых невстраиваемым функциям, недопустимо.
     */
    fun lookForAliceLambda(people: List<Person>) {
        people.forEach {// <- inline fun
            if (it.name == "Alice") {
                println("Found")
                return // <- функция lookForAlice вернёт управление вызывающему коду
            }
        }
        println("Alice is not found")
    }

    lookForAliceLambda(people) // Found

    /**
     * Попытка использования return в не inline функции:
     */
    fun <T> Iterable<T>.myNoInlineForEach(action: (T) -> Unit): Unit {
        for (element in this) action(element)
    }

    /**
     * Использование "return" в лямбда-выражениях, передаваемых невстраиваемым функциям, недопустимо.
     * Невстраиваемая функция может сохранить лямбда выражение в переменной, чтобы выполнить его после завершения, когда
     * лямбда-выражение и его "return" окажутся в другом контексте.
     */
    fun lookForAliceNoInline(people: List<Person>) {
        people.myNoInlineForEach {// <- no inline fun
            if (it.name == "Alice") {
                println("Found")
                 // return <- 'return' is not allowed here. Change to return@myNoInlineForEach
            }
        }
        println("Alice is not found")
    }

    /**
     * Возврат из лямбда-выражений с помощью меток.
     * Лямбда-выражения поддерживают также локальный возврат. Локальный возврат напоминает "break" в "for". Он прерывает работу
     * лямбда-выражения и продолжает выполнение инструкции, следующей сразу за вызовом лямбды.
     * Реализуется локальный возврат с помощью меток.
     */
    fun lookForAliceNoInlineWithLabel(people: List<Person>) {
        people.myNoInlineForEach label@{// <- no inline fun
            if (it.name == "Alice") {
                println("Found")
                return@label // можно return@myNoInlineForEach
            }
        }
        println("Alice is not found")
    }
    lookForAliceNoInlineWithLabel(people) // Found, Alice is not found
    // Выход из внешней функции не произошёл

    /**
     * Анонимные функции - другой способ реализации локального выхода из лямбды.
     *
     * Анонимная функция не имеет имени и типа в объявлении параметров.
     * Внутри анонимной функции "return" выполнит выход из анонимной функции, а не из внешней.
     * Здесь действует правило: "return" производит выход из ближайшей функции, объявленной с ключевым словом fun.
     * Лямбда выражение - без fun, поэтому return в лямбде производит выход из внешней функции.
     * А анонимный функции объявляются с помощью fun.
     */
    fun lookForAliceAnonymous(people: List<Person>) {
        people.forEach(fun (person) {
            if (person.name == "Alice") {
                println("Found")
                return
            }
        })
        println("External fun end")
    }
    lookForAliceAnonymous(people) // Found, External fun end

    /**
     * Анонимные функции - это альтернативный синтаксис оформления лямбда-выражений с иными правилами для return.
     * Пример анонимной функции:
     */
    people.filter(
        fun (person): Boolean {
            return person.age < 30
        }
    )
}