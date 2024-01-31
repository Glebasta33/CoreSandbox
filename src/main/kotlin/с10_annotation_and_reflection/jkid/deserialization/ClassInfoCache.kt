package с10_annotation_and_reflection.jkid.deserialization

import с10_annotation_and_reflection.jkid.serialization.getSerializer
import с10_annotation_and_reflection.jkid.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

/**
 * Цель класса - уменьшить накладные расходы, связанные с использованием механизма рефлексии.
 * Выполнение поиска параметров конструктора для каждого объекта замедлит процесс десериализации,
 * поэтому мы делаем это только 1 раз для каждого класса и сохраняем информацию в кэше.
 */
class ClassInfoCache {
    private val cacheData = mutableMapOf<KClass<*>, ClassInfo<*>>() //<- мы удаляем информацию о типе, когда сохраняем значения в словаре
    //но реализация get гарантирует, что возвращаемый KClass<T> имеет правильный аргумент типа:
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(cls: KClass<T>): ClassInfo<T> =
        cacheData.getOrPut(cls) { ClassInfo(cls) } as ClassInfo<T>//<- getOrPut получает значение, либо, если его нет, вызывает лямбду, чтобы создать новое (ClassInfo)
}

/**
 * Класс отвечает за создание нового экземпляра целевого класса и кэширование необходимой информации.
 */
class ClassInfo<T : Any>(cls: KClass<T>) {
    private val className = cls.qualifiedName
    private val constructor = cls.primaryConstructor
        ?: throw JKidException("Class ${cls.qualifiedName} doesn't have a primary constructor")

    private val jsonNameToParamMap = hashMapOf<String, KParameter>()
    private val paramToSerializerMap = hashMapOf<KParameter, ValueSerializer<out Any?>>()
    private val jsonNameToDeserializeClassMap = hashMapOf<String, Class<out Any>?>()

    /**
     * Код отыскивает свойства, соответствующие параметрам конструктора, и извлекает из аннотации.
     */
    init {
        constructor.parameters.forEach { cacheDataForParameter(cls, it) }
    }

    private fun cacheDataForParameter(cls: KClass<*>, param: KParameter) {
        val paramName = param.name
            ?: throw JKidException("Class $className has constructor parameter without name")

        val property = cls.declaredMemberProperties.find { it.name == paramName } ?: return
        val name = property.findAnnotation<JsonName>()?.name ?: paramName
        jsonNameToParamMap[name] = param

        val deserializeClass = property.findAnnotation<DeserializeInterface>()?.targetClass?.java
        jsonNameToDeserializeClassMap[name] = deserializeClass

        val valueSerializer = property.getSerializer()
            ?: serializerForType(param.type.javaType)
            ?: return
        paramToSerializerMap[param] = valueSerializer
    }

    fun getConstructorParameter(propertyName: String): KParameter = jsonNameToParamMap[propertyName]
        ?: throw JKidException("Constructor parameter $propertyName is not found for class $className")

    fun getDeserializeClass(propertyName: String) = jsonNameToDeserializeClassMap[propertyName]

    fun deserializeConstructorArgument(param: KParameter, value: Any?): Any? {
        val serializer = paramToSerializerMap[param]
        if (serializer != null) return serializer.fromJsonValue(value)

        validateArgumentType(param, value)
        return value
    }

    private fun validateArgumentType(param: KParameter, value: Any?) {
        if (value == null && !param.type.isMarkedNullable) {
            throw JKidException("Received null value for non-null parameter ${param.name}")
        }
        if (value != null && value.javaClass != param.type.javaType) {
            throw JKidException("Type mismatch for parameter ${param.name}: " +
                    "expected ${param.type.javaType}, found ${value.javaClass}")
        }
    }

    /**
     * Метод KCallable.call позволяет вызывать функцию или конструктор (но он не поддерживает именованные аргументы).
     * Метод KCallable.callBy - осуществляет поддержку именованных аргументов:
     *
     *      interface KCallable<out R> : KAnnotatedElement {
     *          fun callBy(args: Map<KParameter, Any?>): R
     *          ...
     *      }
     *
     * callBy позволяет вызывать конструктор и передавать ему словарь с параметрами и соответствующими значениями (важен порядок и тип).
     */
    fun createInstance(arguments: Map<KParameter, Any?>): T {
        ensureAllParametersPresent(arguments)
        return constructor.callBy(arguments)
    }

    private fun ensureAllParametersPresent(arguments: Map<KParameter, Any?>) {
        for (param in constructor.parameters) {
            if (arguments[param] == null && !param.isOptional && !param.type.isMarkedNullable) {
                throw JKidException("Missing value for parameter ${param.name}")
            }
        }
    }
}

class JKidException(message: String) : Exception(message)