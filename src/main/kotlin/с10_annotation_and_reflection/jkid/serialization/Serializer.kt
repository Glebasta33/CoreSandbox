package с10_annotation_and_reflection.jkid.serialization

import с10_annotation_and_reflection.jkid.*
import с10_annotation_and_reflection.jkid.exercise.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * ## 10.2. Рефлексия: интроспекция объектов во время выполнения.
 * Механизм рефлекции помогает обращаться к свойствам и методам объектов динамически, во время выполнения.
 * Обычно обращение к члену класса в исходном коде оформляется как ссылка на конкретное объявление, которую
 * компилятор обрабатывает статистически.
 * Но иногда требуется написать код, который мог бы работать с объектами любых типов, не зная заранее состав их членов.
 *
 * Механизм рефлексии реализован в пакете java.lang.reflect и kotlin.reflect (не являются частью языка по умолчанию).
 * ## Механизм рефлексии в Kotlin: KClass, KCallable, KFunction, KProperty.
 * Главная точка входа в механизм рефлексии Kotlin - это KClass (аналог java.lang.Class). Его можно использовать для
 * доступа ко всем членам класса и его суперклассов.
 * Чтобы получить экземпляр KClass, нужно использовать выражение MyClass::class.
 * Чтобы узнать класс объекта во время выполнения, сначала нужно получить его Java-класс с помощью свойства .javaClass (аналог Object.getClass()).
 * Чтобы выполнить переход между механизмами рефлексии Java и Kotlin, нужно ещё обратиться к свойству .kotlin
 *
 * ... см. Reflection.kt
 *
 */
fun serialize(obj: Any): String = buildString { serializeObject(obj) }

/* the first implementation discussed in the book */
private fun StringBuilder.serializeObjectWithoutAnnotation(obj: Any) {
    val kClass = obj.javaClass.kotlin
    val properties = kClass.memberProperties

    properties.joinToStringBuilder(this, prefix = "{", postfix = "}") { prop ->
        serializeString(prop.name)
        append(": ")
        serializePropertyValue(prop.get(obj))
    }
}

private fun StringBuilder.serializeObject(obj: Any) {
    obj.javaClass.kotlin.memberProperties //<- получить все свойства класса
        .filter { it.findAnnotation<JsonExclude>() == null } //<- отфильтровать те, которые содержат аннотацию JsonExclude
        /**
         * Далее функция проходит по всем свойствам класса и поочерёдно их сериализует.
         */
        .joinToStringBuilder(this, prefix = "{", postfix = "}") {
            serializeProperty(it, obj)
        }
}

private fun StringBuilder.serializeProperty(
    prop: KProperty1<Any, *>, obj: Any
) {
    val jsonNameAnn = prop.findAnnotation<JsonName>()
    val propName = jsonNameAnn?.name
        ?: prop.name //<- Получение имени, указанного в аннотации, если не указана - использовать имя свойства
    serializeString(propName)
    append(": ")

    var value = prop.get(obj) //<- Получение значения свойства

    prop.findAnnotation<DateFormat>()?.let { jsonDateAnn ->
        value = serializeDate(jsonDateAnn, value)
    }

    val jsonValue = prop.getSerializer()?.toJsonValue(value) ?: value //<- Проверка на наличие кастомного сериализатора
    serializePropertyValue(jsonValue)
}

fun KProperty<*>.getSerializer(): ValueSerializer<Any?>? {
    val customSerializerAnn = findAnnotation<CustomSerializer>() ?: return null
    val serializerClass = customSerializerAnn.serializerClass //<- получение типа кастомного сериализатора

    /**
     * Объекты(object) и классы представлены классом KClass.
     * Но единственный экземпляр объекта можно получить через objectInstance: T?
     * createInstance находит пустой конструктор и создаёт экземпляр класса вызвав .call()
     */
    val valueSerializer = serializerClass.objectInstance //<- получение или создание экземпляра кастомного сериализатора
        ?: serializerClass.createInstance()
    @Suppress("UNCHECKED_CAST")
    return valueSerializer as ValueSerializer<Any?>
}

/**
 * Функция проверяет тип значения - примитив, строка, коллекция или иначе объект (для которого рекурсивно операции повторяются из "верхней" функции serializeObject).
 */
private fun StringBuilder.serializePropertyValue(value: Any?) {
    when (value) {
        null -> append("null")
        is String -> serializeString(value)
        is Number, is Boolean -> append(value.toString())
        is List<*> -> serializeList(value)
        else -> serializeObject(value)
    }
}

private fun StringBuilder.serializeList(data: List<Any?>) {
    data.joinToStringBuilder(this, prefix = "[", postfix = "]") {
        serializePropertyValue(it)
    }
}

private fun StringBuilder.serializeString(s: String) {
    append('\"')
    s.forEach { append(it.escape()) }
    append('\"')
}

private fun serializeDate(jsonDateAnn: DateFormat, value: Any?): Any? {
    return SimpleDateFormat(jsonDateAnn.format).format(value as Date)
}

private fun Char.escape(): Any =
    when (this) {
        '\\' -> "\\\\"
        '\"' -> "\\\""
        '\b' -> "\\b"
        '\u000C' -> "\\f"
        '\n' -> "\\n"
        '\r' -> "\\r"
        '\t' -> "\\t"
        else -> this
    }