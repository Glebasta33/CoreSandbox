package c7_operators_overloading

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * ## 7.5. Повторное использование логики обращения к свойству: делегирование свойств.
 * Особенность Kotlin "Делегирование свойств" опирается на Kotlin-соглашения.
 * Делегирование - шаблон проектирования, согласно которому объект не сам выполняет требуемое задание,
 * а делегирует его другому вспомогательному объекту. Такой вспомогательный объект называется делегатом.
 * При делегировании свойств, шаблон применяется к свойствам, которые делегируют логику доступа методам вспомогательных объектов.
 *
 * В общем случае синтаксис делегирования свойств выглядит так:
 *     class Foo {
 *         var p: Type by Delegate()
 *     }
 * Свойство p делегирует логику своих методов доступа другому объекту класса Delegate(), следующим за ключевым словом "by".
 * Компилятор создаст скрытое свойство, хранящее вспомогательный объект Delegate():
 *
 *     class Foo {
 *         private val delegate = Delegate() // <- вспомогательное свойство, сгенерированное компилятором.
 *
 *         var p: Type
 *             set(value) = delegate.setValue(..., value)
 *             get() = delegate.getValue(...)
 *     }
 *
 * Свойство 'p' можно использовать как обычно. Но под капотом будут вызваны методы вспомогательного класса Delegate.
 * При делегировании класс вспомогательного объекта (Delegate) должен иметь методы getValue и setValue (для изменяемых свойств).
 *
 *
 * ## Пример использования делегирования свойств: отложенная инициализация и "by lazy".
 * Отложенная инициализация - шаблон, позволяющий отложить создание объекта до момента, когда он действительно понадобится.
 * Это полезно, когда инициализация потребляет значительные ресурсы.
 *
 * Допустим, нужно самостоятельно реализовать загрузку emails только при первом обращении к полю и только 1 раз.
 */
data class Email(val name: String)

fun loadEmails(person: Person): List<Email> {
    println("Load email for ${person.name}")
    return listOf(Email("${person.name}@yandex.ru"))
}

class Person(val name: String) {
    private var _emails: List<Email>? = null // <- свойство, которому делегируется логика работы свойства emails

    /**
     * Приём называется _теневое свойство (backing property)_.
     * Свойство _emails (nullable) хранит значение, свойство emails открывает к нему доступ для чтения.
     */

    val emails: List<Email>
        get() {
            if (_emails == null) { // загрузка данных при первом обращении:
                _emails = loadEmails(this)
            }
            return _emails!! // <- если данные уже загружены, вернуть их
        }

    /**
     * Котлин предлагает готовое решение - использовать делегированные свойства, инкапсулирующие теневые свойства [lazy].
     * Функция [lazy] возвращает объект, имеющий метод getValue (его можно использовать после by).
     * Параметр функции lazy - лямбда-выражение, которое она вызывает для инициализации значения.
     */
    val emailsLazy by lazy { loadEmails(this) }
}

private fun runExample1() {
    val alice = Person("Alice")
    println(alice.emails) // Load email for Alice... <- загрузка адресов при первом обращении
    println(alice.emails) // <- нет загрузки при повторном обращении
    println(alice.emailsLazy) // Load email for Alice... <- загрузка адресов при первом обращении
    println(alice.emailsLazy) // <- нет загрузки при повторном обращении
}

/**
 * ## Реализация делегированных свойств.
 * Чтобы понять, как реализуется делегирование свойств, нужно рассмотреть ещё один пример:
 * уведомление обработчиков событий, когда свойство объекта изменяет своё значение
 * (это именно частный пример делегатов с дополнительной возможностью отправки уведомлений).
 * Для решения подобных задач в Java есть стандартный механизм: классы [PropertyChangeSupport] и [PropertyChangeEvent].
 * Класс [PropertyChangeSupport] управляет списком обработчиков и передаёт им события [PropertyChangeEvent].
 * Реализуем вспомогательный класс, содержащий поле PropertyChangeSupport:
 */

open class PropertyChangeAware {
    protected val changeSupport = PropertyChangeSupport(this)

    fun appPropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.removePropertyChangeListener(listener)
    }
}

/**
 * Класс Man с 1 неизменяемым и 2 изменяемыми свойствами (класс будет уведомлять обработчиков при их изменении).
 */
class Man(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    var age: Int = age
        set(newValue) {
            /**
             * Логику установки значения можно вынести в отдельный класс [ObservableProperty]
             */
            val oldValue = field
            field = newValue
            changeSupport.firePropertyChange("age", oldValue, newValue) // <- уведомление обработчиков об изменении свойства
        }

    var salary: Int = salary
        set(newValue) {
            val oldValue = field
            field = newValue
            changeSupport.firePropertyChange("salary", oldValue, newValue) // <- уведомление обработчиков об изменении свойства
        }
}


private fun runExample2() {
    val dmitry = Man("Dmitry", 34, 2000)
    dmitry.appPropertyChangeListener { event ->
        println(
            "Property ${event.propertyName} changed " +
                    "from ${event.oldValue} to ${event.newValue}"
        )
    }

    dmitry.age = 35 // Property age changed from 34 to 35
    dmitry.salary = 2500 // Property salary changed from 2000 to 2500

    /**
     * Логику установки значения можно вынести в отдельный класс [ObservableProperty]:
     */
    class ObservableProperty(
        val propName: String, var propValue: Int,
        val changeSupport: PropertyChangeSupport
    ) {
        fun getValue(): Int = propValue
        fun setValue(newValue: Int) {
            val oldValue = propValue
            propValue = newValue
            changeSupport.firePropertyChange(propName, oldValue, newValue)
        }
    }

    /**
     *  Доработанный класс Man:
     */
    class Man(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
        private val _age = ObservableProperty("age", age, changeSupport)
        var age: Int
            get() = _age.getValue()
            set(value) { _age.setValue(value) }

        private val _salary = ObservableProperty("salary", salary, changeSupport)
        var salary: Int
            get() = _salary.getValue()
            set(value) { _salary.setValue(value) }
    }
}

/**
 * Доработанный вспомогательный класс для делегата
 */
class ObservableProperty2(
    var propValue: Int,
    val changeSupport: PropertyChangeSupport
) {
    /**
     * В таком виде должны быть реализованы методы getValue и setValue у делегата,
     * чтобы его можно было указать после ключевого слова "by":
     *
     * operator - нужен, чтобы работало соглашения для "by".
     * KProperty - позволяет определить имя поля.
     */
    operator fun getValue(m: Man2, prop: KProperty<*>): Int = propValue

    operator fun setValue(m: Man2, prop: KProperty<*>, newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
}

/**
 * Теперь класс Man можно переписать с использованием механизма делегирования:
 */
class Man2(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    /**
     * Ключевое слово by заставляет компилятор написать всё то, что было приписано в предыдущих версиях вручную.
     * Объект справа by называется делегатом. Kotlin автоматически сохраняет делегат в теневом свойстве и вызывает методы
     * getValue и setValue делегата при обращении к основному свойству.
     */
    var age: Int by ObservableProperty2(age, changeSupport)
    var salary: Int by ObservableProperty2(salary, changeSupport)
}

/**
 * Вместо реализации наблюдения за свойством вручную,
 * Kotlin предлагает готовое решение: Delegates.observable
 */

class Man3(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    private val observer = {
        prop: KProperty<*>, oldValue: Int, newValue: Int ->
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }

    var age: Int by Delegates.observable(age, observer)
    var salary: Int by Delegates.observable(salary, observer)
}

fun main() {
    runExample1()
    runExample2()
}
