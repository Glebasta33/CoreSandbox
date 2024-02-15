package kotlin_lang.kotlin_in_action._4_class_interface

/**
 * ## 4.3 Методы, сгенерированные компилятором: data-классы и делегирование.
 */

/**
 * ## Data class
 * Каждый класс наследник Any, который имеет 3 универсальных метода: equals, hashCode, toString.
 * При создании класса их реализации нужно переопределить для корректного сравнения объектов, логгирования и т.д.
 */
class Client(val name: String, val postalCode: Int) {
    override fun equals(other: Any?): Boolean {
        if (other !is Client) return false
        if (hashCode() != other.hashCode()) return false
        return name == other.name && postalCode == other.postalCode
    }

    override fun hashCode(): Int = name.hashCode() * 11 + postalCode

    override fun toString(): String = "Client(name=$name, postalCode=$postalCode"
}

/**
 * Если добавить модификатор "data" перед определением класса - equals, hashCode, toString сгенерируются автоматически.
 * ! Свойства, не объявленные в primary constructor, не принимают участия при сравнении на equals и hashCode !!!
 *
 * Также в data-классе создаётся метод copy и реализуется механизм деструктора.
 * Метод copy позволяет создать копию объекта, изменив значения некоторых свойств.
 * Это альтернатива изменению полей объекта mutable класса, и хорошо подходит для immutable классов.
 * Копия имеет свой жизненный цикл и не влияет на код, ссылающийся на исходный экземпляр.
 */
data class DataClient(val name: String, val postalCode: Int) {
    var _x: Int = 0
    constructor(x: Int, name: String, postalCode: Int) : this(name, postalCode) {
        _x = x
    }
}

fun main() {
    var client = DataClient(10,"Mike", 1234)
    client = client.copy(postalCode = 4321)
//    client = client.copy(x = ) //ERROR

    val delegatingArrayList = DelegatingArrayList<String>("one", "two", "three")
    println(delegatingArrayList.size)
    delegatingArrayList.printFirst()

    val delegatingArrayListBy = DelegatingArrayListBy<String>("one", "two", "three")
    delegatingArrayListBy.printFirst()

    val countingSet = CountingSet<Int>()
    countingSet.addAll(listOf(1,1,2))
    println("Added: ${countingSet.objectsAdded}, size: ${countingSet.size}") // Added: 3, size: 2
}

/**
 * ## Делегирование в классах
 * Недостаток наследования - связность кода.
 * Иногда нужно расширить поведение класса, даже если он не предназначен для наследования.
 * Для этого применяется шаблон "Декоратор". Он создаёт новый класс с тем же интерфейсом, что у оригинального класса,
 * который нужно расширить, и сохраняет экземпляр оригинального класса в поле нового класса.
 */
class DelegatingArrayList<T>(vararg values: T) : Collection<T> {
    private val innerList = arrayListOf<T>(*values) // объект, реализующий интерфейс Collection

    override val size: Int get() = innerList.size
    override fun isEmpty(): Boolean = innerList.isEmpty()
    override fun iterator(): Iterator<T> = innerList.iterator()
    override fun containsAll(elements: Collection<T>): Boolean = innerList.containsAll(elements)
    override fun contains(element: T): Boolean = innerList.contains(element)
    fun printFirst() { // новый метод, расширяющий функционал ArrayList
        println(innerList.first())
    }
}

/**
 * Недостаток такого подхода - большой объём шаблонного кода.
 * Kotlin предоставляет полноценную поддержку делегирования.
 * Реализуя интерфейс, можно делегировать реализацию другому объекту с помощью ключевого слова "by".
 */
class DelegatingArrayListBy<T>(
    vararg values: T,
    private val innerList: Collection<T> = arrayListOf<T>(*values)
) : Collection<T> by innerList {
    /**
     * Компилятор автоматически сгенерирует реализации методов, как в [DelegatingArrayList]
     * ...
     */
    fun printFirst() { // новый метод, расширяющий функционал ArrayList
        println(innerList.first())
    }
}

/**
 * Пример коллекции, подсчитывающей количество дублирований при вставке.
 */
class CountingSet<T>(
    val innerSet: MutableCollection<T> = HashSet<T>()
) : MutableCollection<T> by innerSet { // <- делегирование реализации MutableCollection объекту в поле innerSet
    var objectsAdded = 0

    override fun add(element: T): Boolean { // <- собственная реализация вместо делегирования
        objectsAdded++
        return innerSet.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean { // <- собственная реализация вместо делегирования
        objectsAdded += elements.size
        return innerSet.addAll(elements)
    }
}