package kotlin_lang.kotlin_in_action._10_annotation_and_reflection

import kotlin_lang.kotlin_in_action._10_annotation_and_reflection.jkid.JsonExclude
import kotlin_lang.kotlin_in_action._10_annotation_and_reflection.jkid.JsonName
import kotlin_lang.kotlin_in_action._10_annotation_and_reflection.jkid.exercise.DateFormat
import kotlin_lang.kotlin_in_action._10_annotation_and_reflection.jkid.exercise.Person
import kotlin_lang.kotlin_in_action._10_annotation_and_reflection.jkid.serialization.serialize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

fun main() {
    val person = Person("Alice", 29)
    val kClass: KClass<Person> = person.javaClass.kotlin //<- вернёт экземпляр KClass<Person>
    println(kClass.simpleName) // Person
    /**
     * Можно динамически пройти по полям, объявленным внутри класса и получить их имя.
     */
    kClass.memberProperties.forEach {
        val name = it.name
        val value = it.get(person)
        println("$name = $value") // age = 29, name = Alice
    }

    /**
     * В KClass есть много полезных методов для доступа к содержимому класса:
     *
     *      interface KClass<T : Any> {
     *
     *          public actual val simpleName: String?
     *
     *          public actual val qualifiedName: String?
     *
     *          override val members: Collection<KCallable<*>>
     *
     *          public val constructors: Collection<KFunction<T>>
     *
     *          public val nestedClasses: Collection<KClass<*>>
     *
     *          ...
     *     }
     *
     * KCallable - это суперинтерфейс для членов класса. Он объявляет метод call, с помощью которого можно вызывать член:
     *
     *      interface KCallable<out R> {
     *          fun call(vararg args: Any?): R
     *          ...
     *      }
     *
     * Можно использовать call для вызова функции с использованием механизма рефлексии.
     * Выражение ::foo имеет тип KFunction1<Int, Unit> : KCallable, он имеет метод invoke. Цифра 1 означает, что у функции 1 параметр.
     * invoke принимает фиксированное количество параметров, поэтому безопаснее использовать его, а не call.
     */
    val kFunction = ::foo
    kFunction.call(42) // 42
    kFunction.invoke(42)

    /**
     * KFunctionN - это синтетические типы, генерируемые компилятором.
     *
     * KPropertyN имеет метод доступа get.
     */
    val kProperty = ::counter
    kProperty.setter.call(21)
    println(kProperty.get()) // 21

    /**
     * Следующий пример сохраняет ссылку на свойство в переменной memberProperty, а затем вызывает его, чтобы получить
     * значение свойства из конкретного экземпляра person.
     *
     * Поскольку все объявления могут аннотироваться, интерфейсы, предоставляющие объявления во время выполнения, такие как
     * KClass, KFunction, KParameter, наследуют KAnnotatedElement.
     * + см. схему в google: kotlin reflection hierarchy
     */
    val memberProperty = Person::age
    println(memberProperty.get(person)) // 29


    println(serialize(person)) // {"age": 29, "name": "Alice"}


    data class Human(
        @JsonName("name_of_person")
        val name: String,
        @JsonName("age_of_person")
        val age: Int,
        @JsonExclude
        val race: String
    )

    val human = Human("Li", 33, "Asian")
    println(serialize(human)) // {"age_of_person": 33, "name_of_person": "Li"}

    data class PersonWithBithDate(
        val name: String,
        @DateFormat("dd-MM-yyyy") val birthDate: Date
    )

    val value = PersonWithBithDate("Alice", SimpleDateFormat("dd-MM-yyyy").parse("13-02-1987"))
    println(serialize(value))
}

fun foo(x: Int) = println(x)
var counter = 0