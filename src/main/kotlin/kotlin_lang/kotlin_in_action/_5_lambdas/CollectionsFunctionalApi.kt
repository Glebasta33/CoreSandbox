package kotlin_lang.kotlin_in_action._5_lambdas

/**
 * ## 5.2. Функциональный API для работы с коллекциями.
 */
data class Person(val name: String, val age: Int)

fun main() {
    val people = listOf(Person("Alice", 27), Person("Bob", 31), Person("John", 27))
    people.filter { it.age > 30 }.map(Person::name).let { println(it) } // Bob

    val canBeInClub27 = { p: Person -> p.age <= 27}
    println(people.all(canBeInClub27)) // false. all - все элементы соответствуют условию?
    println(people.any(canBeInClub27)) // true. any - хотя бы один элемент соответствует условию?

    val mapOfGroups: Map<Int, Collection<Person>> = people.groupBy { it.age }
    println(mapOfGroups) // {27=[Person(name=Alice, age=27), Person(name=John, age=27)], 31=[Person(name=Bob, age=31)]}

    val strings1 = listOf("a", "b", "c")
    val strings2 = listOf("d", "e", "f") // flatten преобразует 2 коллекции в 1
    val strings1and2 = listOf(strings1, strings2).flatten()
    println(strings1and2) // [a, b, c, d, e, f]
    // flatMap = flatten c возможностью модификации элементов
    val strings1and2modified = listOf(strings1, strings2).flatMap { it.map { it.toUpperCase() } }
    println(strings1and2modified) // [A, B, C, D, E, F]
}