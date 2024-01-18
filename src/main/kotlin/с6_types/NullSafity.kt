package с6_types

import kotlin_in_action.JavaPerson
import kotlin_in_action.types.TypesJavaExample

/**
 * ## 6.1. Поддержка значений null
 *
 * Поддержка значений null помогает избежать NullPointerException в runtime, переводя подобные ошибки на этап компиляции.
 * Все типы в Kotlin могут быть nullable и non-nullable. Это разделение даёт понимание, какие операции можно выполнять со значениями.
 */

fun main() {

    // Оператор безопасного вызова: ?.
    fun printAllCaps(s: String?) {
        val allCaps: String? = s?.toUpperCase()
        println(allCaps)
    }
    printAllCaps("abc") // ABC
    printAllCaps(null) // null - если переменная равна null, результат вызова у неё метода будет null

    // Оператор Элвис ?: - оператор замены null значениями по умолчанию...

    // Дженерики в Kotlin автоматически поддерживают нуллабельность (даже если не указать знак вопроса):
    fun <T> printHashCode(t: T) {
        println(t?.hashCode())
    }
    printHashCode(null) // null
    // Чтобы запретить параметру принимать значение null, нужно определить соответсвующую верхнюю границу.
    fun <T : Any> printHashCodeRestricted(t: T) {
        println(t.hashCode())
    }
    //printHashCodeRestricted(null) <- Null can not be a value of a non-null type TypeVariable(T)

    // При использовании типов из Java:
    // - тип становится nullble, если помечен аннотацией @Nullable
    // - типа становится non-nullable, если помечен аннотацией @NotNull
    // В остальных случаях тип считается платформенным, и его можно использовать одновременно как нуллабельный и ненуллабельный -
    // ответственность ложится на разработчика (как в Java).

    //TypesJavaExample().strLen(null) // Cannot invoke "String.length()" because "s" is null

    val personNull = JavaPerson(null)
    val personBob = JavaPerson("Bob")

    personNull.name.length
    personBob.name?.length
}