package kotlin_lang.kotlin_in_action._10_annotation_and_reflection.jkid.deserialization

import kotlin_lang.kotlin_in_action._10_annotation_and_reflection.jkid.*
import java.io.Reader
import java.io.StringReader
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

inline fun <reified T : Any> deserialize(json: String): T {
    return deserialize(StringReader(json))
}

inline fun <reified T : Any> deserialize(json: Reader): T {
    return deserialize(json, T::class)
}

fun <T : Any> deserialize(json: Reader, targetClass: KClass<T>): T {
    val seed = ObjectSeed(targetClass, ClassInfoCache())
    Parser(json, seed).parse()
    return seed.spawn()
}

/**
 * Объект или массива, которые десериализуется в данный момент.
 */
interface JsonObject {
    fun setSimpleProperty(propertyName: String, value: Any?)

    fun createObject(propertyName: String): JsonObject

    fun createArray(propertyName: String): JsonObject
}

/**
 * Универсальная реализация паттерна Builder.
 */
interface Seed : JsonObject {
    val classInfoCache: ClassInfoCache

    /**
     * spawn ("прорастить") возвращает созданный экземпляр по окончании процесса конструирования.
     * (аналог метода build).
     */
    fun spawn(): Any?

    fun createCompositeProperty(propertyName: String, isList: Boolean): JsonObject

    override fun createObject(propertyName: String) = createCompositeProperty(propertyName, false)

    override fun createArray(propertyName: String) = createCompositeProperty(propertyName, true)
}

fun Seed.createSeedForType(paramType: Type, isList: Boolean): Seed {
    val paramClass = paramType.asJavaClass()

    if (List::class.java.isAssignableFrom(paramClass)) {
        if (!isList) throw JKidException("An array expected, not a composite object")
        val parameterizedType = paramType as? ParameterizedType
            ?: throw UnsupportedOperationException("Unsupported parameter type $this")

        val elementType = parameterizedType.actualTypeArguments.single()
        if (elementType.isPrimitiveOrString()) {
            return ValueListSeed(elementType, classInfoCache)
        }
        return ObjectListSeed(elementType, classInfoCache)
    }
    if (isList) throw JKidException("Object of the type ${paramType.typeName} expected, not an array")
    if (Map::class.java.isAssignableFrom(paramClass)) {
        val parameterizedType = paramType as? ParameterizedType ?: throw UnsupportedOperationException("Unsuported parameter type $this")

        val elementType = parameterizedType.actualTypeArguments[1]
        return MapSeed(elementType, classInfoCache)
    }
    return ObjectSeed(paramClass.kotlin, classInfoCache)
}

/**
 * Для конструирования объекта используются данные в параметрах конструктора.
 * При конструировании используются 2 словаря:
 * - valueArguments - для простых значений. setSimpleProperty - добавляет новые значения в словарь.
 * - seedArguments - для составных значений. createCompositeProperty - добавляет новые значения.
 */
class ObjectSeed<out T : Any>(
    targetClass: KClass<T>,
    override val classInfoCache: ClassInfoCache //<- создаёт объекты с использованием рефлексии.
) : Seed {

    private val classInfo: ClassInfo<T> = classInfoCache[targetClass]

    private val valueArguments = mutableMapOf<KParameter, Any?>()
    private val seedArguments = mutableMapOf<KParameter, Seed>()

    private val arguments: Map<KParameter, Any?>
        get() = valueArguments + seedArguments.mapValues { it.value.spawn() } //<-рекурсия

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        val param = classInfo.getConstructorParameter(propertyName)
        valueArguments[param] = classInfo.deserializeConstructorArgument(param, value)
    }

    override fun createCompositeProperty(propertyName: String, isList: Boolean): Seed {
        val param = classInfo.getConstructorParameter(propertyName)
        val deserializeAs = classInfo.getDeserializeClass(propertyName)
        val seed = createSeedForType(
            deserializeAs ?: param.type.javaType, isList
        )
        return seed.apply { seedArguments[param] = this }
    }

    override fun spawn(): T =
        classInfo.createInstance(arguments) //<- создание экземпляра targetClass с передачей словаря аргументов.
}

class ObjectListSeed(
    val elementType: Type,
    override val classInfoCache: ClassInfoCache
) : Seed {
    private val elements = mutableListOf<Seed>()

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        throw JKidException("Found primitive value in collection of object types")
    }

    override fun createCompositeProperty(propertyName: String, isList: Boolean) =
        createSeedForType(elementType, isList).apply { elements.add(this) }

    override fun spawn(): List<*> = elements.map { it.spawn() }
}

class ValueListSeed(
    elementType: Type,
    override val classInfoCache: ClassInfoCache
) : Seed {
    private val elements = mutableListOf<Any?>()
    private val serializerForType = serializerForBasicType(elementType)

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        elements.add(serializerForType.fromJsonValue(value))
    }

    override fun createCompositeProperty(propertyName: String, isList: Boolean): Seed {
        throw JKidException("Found object value in collection of primitive types")
    }

    override fun spawn() = elements
}

class MapSeed(
    val elementType: Type,
    override val classInfoCache: ClassInfoCache
) : Seed {
    private val valueMap = mutableMapOf<String, Any?>()
    private val seedMap = mutableMapOf<String, Seed>()

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        valueMap[propertyName] = value
    }

    override fun createCompositeProperty(propertyName: String, isList: Boolean): JsonObject =
        createSeedForType(elementType, isList).apply { seedMap[propertyName] = this }

    override fun spawn(): Map<String, Any?> =
        valueMap + seedMap.mapValues { it.value.spawn() }
}