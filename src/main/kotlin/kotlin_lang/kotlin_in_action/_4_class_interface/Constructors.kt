package kotlin_lang.kotlin_in_action._4_class_interface

import javax.naming.Context
import javax.swing.text.AttributeSet

/**
 * ## 4.2 Объявление классов с нетривиальными конструкторами или свойствами.
 */

/**
 * В Kotlin, как и в Java, можно объявить несколько конструкторов в классе.
 * Но Kotlin различает основной конструктор и вторичный конструктор (объявляется в теле класса).
 */

/**
 * Основной конструктор делает 2 вещи:
 * - определяет параметры (params) конструктора
 * - определяет свойства (properties), которые инициализируются этими параметрами
 *
 * (Тут nickname - это скорее параметр конструктора, несмотря на val. Просто это сокращённая форма записи).
 */
open class SimpleUser(val nickname: String)

/**
 * Тот же самый класс, но с явной передачей параметра и инициализацией свойства:
 */
open class DetailedUser constructor(_nickname: String) {
    val nickname: String

    init { // Блок инициализации. Выполняется вместе с первичным конструктором.
        nickname = _nickname
        // Либо this.nickname = nickname
    }
}

/**
 * При наследовании основной класс должен инициализировать свойства суперкласса, передав параметры в его конструктор:
 */
class TwitterUser(nickname: String) : SimpleUser(nickname)

/**
 * Если вообще не объявить конструктор, компилятор создаст конструктор по-умолчанию, который ничего не делает и не имеет параметров.
 * При наследовании необходимо явно вызывать конструктор суперкласса, даже если он не имеет параметров.
 */
open class Toggle
class SwitchingToggle : Toggle()

/**
 * Объявление вторичных конструкторов осуществляется ключевым словом "constructor" в теле класса:
 */
open class AnotherView { // нет первичного конструктора
    constructor(ctx: Context) {} // 1-й вторичный конструктор
    constructor(ctx: Context, attr: AttributeSet) {} // 2-й вторичный конструктор
}

/**
 * Для наследования необходимо объявить те же вторичные конструкторы.
 */
class AnotherButton : AnotherView {
    constructor(ctx: Context) : super(ctx) {}
    constructor(ctx: Context, attr: AttributeSet) : super(ctx, attr) {}
//    constructor(ctx: Context) : this(ctx, MY_STYLE) {} - так первый конструктор делегирует инициализацию второму
//    Если класс не имеет первичного конструктора, его вторичные конструкторы должны либо инициализировать базовый класс (super()),
//    либо делегировать выполнение другому конструктору (this()).
}

/**
 * Интерфейсы могут включать объявления абстрактных свойств.
 */
interface User {
    val nickname: String // абстрактное свойство
}

/**
 * Сам интерфейс не может иметь состояние, поэтому реализующие его должны сами предоставить доступ свойству.
 * Это можно сделать несколькими способами:
 */
class PrivateUser(override val nickname: String) : User
class SubscibingUser(val email: String) : User {
    override val nickname: String
        get() = email.substringBefore("@")
}

/**
 * По умолчанию методы видимости имеют ту же видимость, что и свойство.
 * Но её можно изменить, добавив модификатор видимости.
 */
class LengthCounter {
    var counter: Int = 0
        private set //теперь изменять значение поля можно только изнутри класса

    fun addWord(word: String) {
        counter += word.length
    }
}