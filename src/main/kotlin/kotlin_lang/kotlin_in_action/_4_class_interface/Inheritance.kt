package kotlin_lang.kotlin_in_action._4_class_interface

import java.io.Serializable

/**
 * # 4.1 Создание иерархий классов
 */

/**
 * Интерфейсы в Kotlin могут содержать как абстрактные методы, так и методы с реализацией.
 * Интерфейсы в Kotlin не могут иметь состояния.
 */

interface Clickable {
    fun click() // абстрактный метод, который должны реализовать классы, реализующие интерфейс.
    fun showOff() = println("clicked") // метод с реализацией по-умолчанию.
}

interface Focusable {
    fun setFocus(b: Boolean) = println("isFocused: $b") // метод с реализацией по-умолчанию.
    fun showOff() = println("focused") // метод с реализацией по-умолчанию.
}

class Button : Clickable, Focusable { /* реализация Focusable - выдаст ошибку because it inherits multiple interface methods*/
    override fun click() { // реализация метода интерфейса
        println("Button was clicked")
    }

    override fun showOff() { //Для конкретных методов с одинаковым названием нужно явно его вызвать для соответствующего интерфейса
        super<Clickable>.showOff() // В Java: Clickable.super.showOff();
        super<Focusable>.showOff()
    }
}

fun main() {
    val button = Button()
    button.click()
    button.setFocus(true)
    button.showOff() //clicked и focused


    val nestedClass = SomeButton.ButtonState() // вызывается как статический член

    val innerClass = Outer().Inner() // вызывается как динамический член
    innerClass.getOuterReference().outerClassField
}

/**
 * В Kotlin все классы и методы по умолчанию final.
 * Чтобы наследоваться от класса или переопределить метод класса, необходимо пометить их ключевым словом open.
 */
open class RichButton : Clickable {
    fun disable() {} // закрытая функция - её невозможно переопределить в подклассе
    open fun animate() {} // открытая функция - её можно переопределить в подклассе
    override fun click() {} // переопределение открытой функции также является открытым
}
class SubRichButton : RichButton()

/**
 * Абстрактный класс всегда считается open.
 */
abstract class Animated {
    abstract fun animate() // абстрактная функция, должна быть определена в наследнике
    fun animateTwice() {} // конкретная функция в абстрактном классе по-умолчанию закрыта
    open fun stopAnimating() {} // конкретную функцию можно сделать открытой
}
class AnimatedView : Animated() {
    override fun animate() {} // обязательная реализация абстрактной функции
}

/**
 * ## Модификаторы доступа (управляют наследованием) в классе:
 * _final_ - не может быть переопределён. Применяется ко всем членам класса по-умолчанию.
 * _open_ - может быть переопределён. Указывается явно.
 * _abstract_ - должен быть переопределён. Исп.-ся только в абстрактных классах.
 * _override_ - переопределяет метод суперкласса или интерфейса. По умолчанию открыт, если дополнительно не помечен final.
 */

/**
 * ## Модификаторы видимости (управляют доступностью объектов в коде) в Kotlin:
 * _public (по умолчанию)_ - Доступен повсюду.
 * _internal_ - Доступен только в модуле.
 * _protected_ - Доступен в подклассах.
 * _private_ - - Доступен в классе или файле.
 *
 * При компиляции в байт-код Java, public, protected и private сохраняются.
 * private class будет скомпилирован с областью видимости пакета (в Java нет приватных классов).
 * internal будет скомпилирован в public.
 */

/**
 * Kotlin запрещает ссылаться из функции с более узкой областью видимости, на класс с более широкой областью видимости.
 * Можно обращаться к другим сущностям из той же или более широкой области видимости.
 */
internal open class TalkativeButton {}
//fun TalkativeButton.print() {}

/**
 * ## Вложенные и внутренние классы в Java и Kotlin.
 * Такие класс полезны для скрытия вспомогательных классов и размещения кода ближе к месту использования.
 *
 * Отличие: в Kotlin вложенные классы не имеют доступа к экземпляру, если не запросить этого явно с помощью ключевого слова "inner".
 *
 * Вложенный класс (не содержит ссылки на внешний класс): static class A (в Java) == class A (в Kotlin)
 * Внутренний класс (содержит ссылку на внешний класс): class A (в Java) == inner class A (в Kotlin)
 */

interface State : Serializable // вспомогательный класс для сериализации части стейта View.
interface View { //не сериализуемый класс
    fun getCurrentState(): State
    fun restoreState(state: State)
}
class SomeButton : View {
    override fun getCurrentState(): State = ButtonState()

    override fun restoreState(state: State) { /*...*/  }

    /*inner*/ class ButtonState : State { /*...*/ } // вложенный класс (как static в Java не ссылается на внешний (не сериализуемый класс) - ошибки не будет)
}

class Outer {
    val outerClassField: String = "str"
    inner class Inner {
        fun getOuterReference(): Outer = this@Outer // обращение к внешнему классу
    }
}

/**
 *     val nestedClass = SomeButton.ButtonState() // вызывается как статический член
 *
 *     val innerClass = Outer().Inner() // вызывается как динамический член
 *     innerClass.getOuterReference().outerClassField
 */