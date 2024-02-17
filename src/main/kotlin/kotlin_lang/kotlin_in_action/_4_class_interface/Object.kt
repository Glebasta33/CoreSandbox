package kotlin_lang.kotlin_in_action._4_class_interface

import java.io.File

/**
 * ## 4.4. Ключевое слово object: совместное объявление класса и его экземпляра.
 */

/**
 * ## Реализация шаблона Singleton.
 */
object MyObject { // <- Не может быть конструктора
    init { println("MyObject init") }
}

/**
 * Объект может наследовать классы и интерфейсы.
 */
object CaseInsensitiveFileComparator : Comparator<File> {
    override fun compare(o1: File, o2: File): Int {
        return o1.path.compareTo(o2.path, ignoreCase = true)
    }
}

fun main() {
    MyObject.toString() // MyObject init
    /**
     * object можно использовать в любом контексте, где используется обычный экземпляр класса,
     * — например, передавать объект в качестве аргумента функции.
     */
    val files = listOf(File("/Z"), File("/a"))
    println(files.sortedWith(CaseInsensitiveFileComparator)) // [\a, \Z]

    /**
     * object компилируется в Java в виде стандартного синглтона со статическим полем хранящим единственный экземпляр,
     * который всегда называется INSTANCE. Из Java обращение к object выглядело бы так:
     * CaseInsensitiveFileComparator.INSTANCE
     */

    val persons = listOf(Person("Bob"), Person("Alice"))
    println(persons.sortedWith(Person.NameComparator)) // [Person(name=Alice), Person(name=Bob)]

    A.InnerObject.bar() // InnerObject called
    A.bar() // companion object called

    println(UserFactory.newSubscribingUser("vasya1993@gmail.com").nickname)

    loadFromJSON(PersonWithCompanion)
}

/**
 * Объекты можно объявлять в классах. Такой объект будет единственным для всех экземпляров класса.
 */
data class Person(val name: String) {
    object NameComparator : Comparator<Person> {
        override fun compare(o1: Person, o2: Person): Int =
            o1.name.compareTo(o2.name)
    }
}

/**
 * ## companion object: место для фабричных методов и статических членов класса.
 * Классы в Kotlin не могут иметь статических членов. В Kotlin нет ключевого слова "static".
 * Замена: функции уровня пакета и object.
 * В большинстве случаев достаточно функций уровня пакета, но они не имеют доступа к приватным членам класса.
 * Поэтому, чтобы написать функцию, которую можно вызывать без экземпляра класса, но с доступом к внутреннему устройству класса,
 * нужно сделать её членом объекта объявленного внутри класса.
 * Чтобы обращаться к членам объекта внутри класса напрямую через имя класса используется ключевое слово "companion".
 */
class A {
    object InnerObject {
        fun bar() { println("InnerObject called") }
    }
    companion object {
        fun bar() { println("companion object called") }
    }
    /**
     *     A.InnerObject.bar() // InnerObject called
     *     A.bar() // companion object called
     */
}

/**
 * companion object (+ private constructor) идеально подходит для реализации шаблона "Фабрика".
 */
class UserFactory private constructor(val nickname: String) {
    companion object {
        fun newUser(nickname: String) = UserFactory(nickname)
        fun newSubscribingUser(email: String): UserFactory {
            if (!email.contains("@")) throw IllegalArgumentException("not email")
            return UserFactory(email.substringBefore("@"))
        }
    }
}

/**
 * companion object может реализовывать интерфейс. В таком случае можно будет передавать класс как объект, реализующий интерфейс.
 */
interface JSONFactory<T> {
    fun fromJSON(jsonText: String): T
}

class PersonWithCompanion(val name: String) {
    companion object : JSONFactory<PersonWithCompanion?> {
        override fun fromJSON(jsonText: String): PersonWithCompanion? {
             return null
        }
    }
}

fun <T> loadFromJSON(factory: JSONFactory<T>) { }
// Можно передавать таким образом: loadFromJSON(PersonWithCompanion)

/**
 * ## Анонимные объекты.
 * Анонимные объекты заменяют анонимные внутренние классы в Java.
 * Анонимные объекты способны изменять значения переменных в области видимости, где были созданы (в отличие от Java).
 */