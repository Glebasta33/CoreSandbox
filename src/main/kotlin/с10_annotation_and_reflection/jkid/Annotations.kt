package с10_annotation_and_reflection.jkid

import kotlin.reflect.KClass

/**
 * ## Синтаксис объявления аннотации
 *
 * Модификатор "annotation" используется для определения структур метаданных, они не могут содержать программного кода.
 * Соответственно, компилятор не допускает определения тела для класса-аннотации.
 *
 * Параметры класса-аннотации объявляются в основном конструкторе. val - обязательно для все параметров.
 *
 * Эта же аннотация на Java:
 *
 *      public @interface JsonName {
 *          String value();
 *      }
 *
 * Аннотации, применяемые к классам-аннотациям называют метааннотациями (тут метааннотация - @Target, определяет тип элементов, к которым может применяться аннотация).
 */
@Target(AnnotationTarget.PROPERTY)
annotation class JsonName(val name: String)

@Target(AnnotationTarget.PROPERTY)
annotation class JsonExclude

/**
 * Иногда требуется возможность ссылаться на класс как объявление метаданных.
 * Данная аннотация DeserializeInterface позволяет десериализовать свойство с типом интерфейса.
 * Нельзя создать экземпляр интерфейса, поэтому нужно явно указать класс, который будет использован для создания экземпляра.
 * Вот как используется эта аннотация:
 *
 *      @DeserializeInterface(CompanyImpl::class) val company: Company
 *
 * Тип KClass - это Kotlin-версия типа java.lang.Class. Он используется для хранения ссылок на классы Kotlin.
 * Типовой параметр KClass определяет на какие классы может ссылать данная ссылка.
 * Например, CompanyImpl::class имеет тип KClass<CompanyImpl>.
 * KClass<CompanyImpl> является подтипом KClass<out Any> благодаря ковариантности.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class DeserializeInterface(val targetClass: KClass<out Any>)

/**
 * CustomSerializer принимает в качестве аргумента ссылку на класс, который должен реализовать интерфейс ValueSerializer.
 *
 *  KClass<out Интерфейс> - указывает, что аргументом аннотации может выступать любой класс, реализующий этот интерфейс.
 *  <*> - так как интерфейс имеет типовые аргументы, указываем, что допускаются любые значения.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class CustomSerializer(val serializerClass: KClass<out ValueSerializer<*>>)

interface ValueSerializer<T> {
    fun toJsonValue(value: T): Any?
    fun fromJsonValue(jsonValue: Any?): T
}