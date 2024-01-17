package c5_lambdas

import с4_class_interface.Person

/**
 * ## 5.1. Лямбда-выражения и ссылки на члены класса.
 *
 * Очень часто приходится решать задачу передачи некоторого поведения в коде.
 * В Java (до 8 версии) такие задачи решались с помощью анонимных внутренних классов:
 *         button.setOnClickListener(new OnClickListener() {
 *             @Override
 *             public void onClick() {
 *                  //действия по щелчку
 *             }
 *         });
 *          Лямбда после Java 8:
 *        button.setOnClickListener(() -> {
 *              //действия по щелчку
 *        });
 *
 * Функциональный подход предполагает возможность использования функции в качестве значений.
 * Вместо объявления класса и передачи его экземпляра (реализующего нужное поведение функции),
 * можно передать функцию напрямую:
 *        button.setOnClickListener { ... }
 */

data class MyPerson(val name: String, val age: Int)
fun main() {
    val people = listOf(MyPerson("Bob", 33), MyPerson("Alice", 29))
    /**
     * Функция maxBy принимает один аргумент - функцию, принимающую значения для сравнения.
     * Код внутри { it.age } - лямбда-выражение: в качестве аргумента принимает элемент коллекции и возвращает значение для сравнения.
     */
    println(people.maxBy { it.age })
    println(people.maxBy(selector = { person: MyPerson -> person.age })) // maxBy без сокращений
    println(people.maxBy(MyPerson::age)) // member reference

    /**
     * Синтаксис лямбда-выражения.
     * Список аргументов (x: Int, y: Int) отделён от тела лямбда-выражения (x + y) стрелкой.
     */
    val sum = { x: Int, y: Int -> x + y }
    println(sum(1, 2))

    /**
     * ## Доступ к переменным и захват mutable-переменных.
     * Когда анонимный класс объявляется внутри функции, он имеет доступ к параметрам и локальным переменным этой функции.
     * В лямбда-выражениях можно делать то же самое.
     */
    fun printMessagesWithPrefix(messages: Collection<String>, prefix: String) {
        messages.forEach { // <- лямбда выражение:
            println("$prefix $it") // <- обращение к параметру prefix
        }
    }
    val errors = listOf("403 Forbidden", "404 Not Found")
    printMessagesWithPrefix(errors, "Error:")

    /**
     * В Java подобный доступ был возможен только к финальным переменным (её значение просто копировалось).
     * В Kotlin возможно изменять mutable-переменные внутри лямбда-выражений - это называется захватом переменных из лямбды.
     * Под капотом захват переменной осуществляется с помощью примерно такой обёртки:
     * class Ref<T>(var value: T) <- класс-обёртка для захвата переменной
     * val counter = Ref(0) <- переменная Ref - финальная и может быть захвачена
     * val inc = { counter.value++ } <- формально захватывается неизменяемая переменная (обёртка), хранящая внутри изменяемую
     */


    println(people.maxBy(MyPerson::age)) // member reference
    /**
     * Member reference - ссылки на члены класса.
     * Лямбда-выражение позволяет передать блок кода как аргумент функции.
     * Если блок кода, который нужно передать, уже определён как метод класса, можно передать его напрямую с помощью "::"
     * MyPerson::age - это ссылка на член класса. Синтаксис: "[Класс]::[Член класса, на который нужно сослаться (метод или свойство)]
     * MyPerson::age == { person: MyPerson -> person.age }
     *
     * Также можно создать ссылку на функцию верхнего уровня:
     */
    run(::salute)

    /**
     * Можно сохранить и отложить операцию создания экземпляра класса с помощью ссылки на конструктор:
     */
    val createPerson = ::Person
    val p = createPerson("Bob")
    println(p)

    fun Person.isCapitalized() = name.first().isUpperCase()
    val predicate = Person::isCapitalized // <- ссылку можно получить и на extension-функцию
    println(p.let(predicate)) //true

    val personName = p::name
    println(personName())
}

fun salute() = println("Salute!")