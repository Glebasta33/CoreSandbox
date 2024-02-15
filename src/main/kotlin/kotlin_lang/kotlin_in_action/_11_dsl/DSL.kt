package kotlin_lang.kotlin_in_action._11_dsl

import java.time.LocalDate
import java.time.Period

/**
 * # Глава 11. Конструирование DSL (Domain Specific Languages).
 * ## 11.1. От API к DSL.
 *
 * Цель разработчиков - достичь максимальной читаемости и выразительности кода.
 * Это цели невозможно достичь, сосредоточив всё внимание только на отдельных классах.
 * Большая часть кода в классах взаимодействует с другими классами, поэтому важно уделять внимание программным интерфейсам классов.
 * Создание выразительных и удобных API - прерогатива не только создателей библиотек, но и каждого разработчика: каждый класс
 * подобно библиотекам предоставляет другим классам возможность взаимодействовать с ним.
 *
 * В идеале API класса должен быть ясным:
 * - читателям кода должно быть ясно, что он делает.
 * - код должен быть простым и не перегруженным избыточными синтаксическими конструкциями.
 *
 * ## Понятие предметно-ориентированного языка.
 * Понятие DSL появилось почти одновременно с понятием ЯП как таковых.
 * Можно выделить ЯП:
 * - общего назначения - для решения практически любых задач с помощью компьютера
 * - предметно-ориентированные (DSL) - ориентированные на решение задач из конкретной предметной области
 * и не обладающие средствами для решения любых других задач (Примеры: SQL, Regex).
 *
 * Особенности DSL:
 * - В DSL-языках увеличивается эффективность в достижении цели за счёт ограничения технических возможностей: на DSL можно
 * более кратко выразить предметную операцию, чем на языке общего назначения.
 * - DSL склоняются к декларативному стилю, тогда как языки общего назначения - к императивному.
 * Императивный язык - описывает последовательность действий, которые требуется выполнить для завершения операции.
 * Декларативный язык - описывает желаемый результат, оставляя детали его получения на усмотрения движка, который интерпретирует этот код.
 * - DSL - имеют существенный недостаток: код на DSL трудно встраивается в приложения, написанные на языках общего назначения.
 *
 * ## Внутренние предметно-ориентированные языки.
 * В противоположность внешним DSL (SQL, ...), внутренние DSL - это часть программы, написанной на языке общего назначения,
 * с таким же синтаксисом.
 * Мы не можем вставить SQL код в программу. Обычно лучшее, на что можно рассчитывать - поместить SQL-запрос в строку.
 * Внутренний DSL позволяет, например, писать SQL запросы на языке Kotlin.
 *
 * ## Структура предметно-ориентированных языков.
 * Не существует чётких границ между DSL и обычным API, и часто критерием становится субъективное мнение.
 * Но часто DSL обладают структурой и грамматикой.
 *
 * Типичный API состоит из набора методов. Использующий их клиент вызывает методы по одному, при этом последовательность вызовов не имеет предопределённой структуры,
 * а контекст выполняемых операций не сохраняется между вызовами. Такие API иногда называются командными API.
 *
 * Вызовы методов в DSL образуют более крупные структуры, определяемые грамматикой DSL.
 * Наличие грамматики (как структура предложения в естественных языках: подлежащее, сказуемое) - вот что позволяет называть внутренний DSL языком.
 *
 * Одно из преимуществ наличия структуры в DSL - она позволяет использовать общий контекст в нескольких вызовах функций, не воссоздавая его заново в каждом вызове.
 *
 * В число особенностей Kotlin, помогающих создавать DSL, входят: функции-расширения, инфиксный вызовы, сокращённые
 * синтаксис лямбд (последний параметр) и перегрузка операторов.
 *
 * ## 11.2. Создание структурированных API: лямбда-выражение с получателем в DSL.
 * Лямбда-выражение с получателем ("лямбда с ресивером") - мощная фича Kotlin, позволяющая конструировать API с определённой структурой.
 *
 * В данном аналоге buildString параметр - обычный функциональный тип, принимающий обычное лямбда-выражение.
 */
fun buildString1(
    builderAction: (StringBuilder) -> Unit
): String {
    val sb = StringBuilder()
    builderAction(sb) //<- передача sb лямбда выражению в качестве аргумента
    return sb.toString()
}

/**
 * Параметр следующей функции имеет тип функции-расширения. StringBuilder тут называется типом получателя (receiver type),
 * а значение этого типа, что передаётся в лямбда-выражение, - объектом-получателем (receiver object).
 */
fun buildString2(
    builderAction: StringBuilder.() -> Unit //<- лямбда с ресивером
): String {
    val sb = StringBuilder()
    sb.builderAction() //<- передача sb лямбда выражению в качестве получателя
    return sb.toString()
}

/**
 * В первом примере мы вынуждены использовать it в теле лямбда выражения, при каждом вызове метода StringBuilder.
 * Во втором внутри лямбды можно обращаться непосредственно к объекту StringBuilder.
 */
fun e1() {
    val s1 = kotlin_lang.kotlin_in_action._11_dsl.buildString1 {
        it.append("Hello, ")
        it.append("World!")
    }
    println(s1) // Hello, World!

    val s2 = kotlin_lang.kotlin_in_action._11_dsl.buildString2 {
        this.append("Hello, ")
        append("World!")
        appendExcl()
    }
    println(s2) // Hello, World!!
}

/**
 * Функции-расширения и лямбда-выражения с получателем получают объект-получатель, который должен быть передан в вызов функции
 * и доступ в её теле. Фактически тип функции-расширения описывает блок кода, который можно вызвать как функцию-расширение.
 *
 * Также можно объявить переменную, хранящую лямбду с ресивером:
 */
val appendExcl: StringBuilder.() -> Unit = { this.append("!") }

/**
 * Функции apply и with основаны на лямбда-выражении с получателем.
 *
 *      inline fun <T> T.apply(block: T.() -> Unit): T {
 *          block() //<- эквивалентно this.block - взывает лямбду с ресивером
 *          return this //<- возвращает объект-получатель
 *      }
 *
 *      inline fun <T, R> with(receiver: T, block: T.() -> R): R {
 *          return receiver.block() //<- возвращает результат лямбды
 *      }
 *
 * Функция apply объявлена как расширение типа получателя, тогда как with принимает объект-получатель в первом аргументе.
 * Разница в том, что apply возвращает сам объект-получатель, а with возвращает результат лямбды.
 * Если результат не имеет значения функции можно считать взаимозаменяемыми:
 */
fun e2() {
    val map = mutableMapOf(1 to "one")
    map.apply { this[2] = "two" }
    with(map) { this[3] = "three" }
    println(map) // {1=one, 2=two, 3=three}
}

/**
 * Далее представлен пример DSL для создания HTML-таблиц.
 */
open class Tag(val name: String) {
    private val children = mutableListOf<kotlin_lang.kotlin_in_action._11_dsl.Tag>()

    /**
     * Вызывает лямбду с ресивером на объекте-ресивере и сохраняет его в список потомков внешнего тега.
     */
    protected fun <T : kotlin_lang.kotlin_in_action._11_dsl.Tag> doInit(child: T, init: T.() -> Unit) {
        child.init()
        children.add(child)
    }

    override fun toString(): String = "<$name>${children.joinToString("")}</$name>"
}

/**
 * Создаёт новый экземпляр TABLE, применяя к нему лямбду с ресивером.
 */
fun table(init: kotlin_lang.kotlin_in_action._11_dsl.TABLE.() -> Unit) = kotlin_lang.kotlin_in_action._11_dsl.TABLE().apply(init)

class TABLE : kotlin_lang.kotlin_in_action._11_dsl.Tag("table") {
    /**
     * Создаёт тег TR, взывает на нём лямбду, и добавляет его в список потомков TABLE
     */
    fun tr(init: kotlin_lang.kotlin_in_action._11_dsl.TR.() -> Unit) = doInit(kotlin_lang.kotlin_in_action._11_dsl.TR(), init)
}

class TR : kotlin_lang.kotlin_in_action._11_dsl.Tag("tr") {
    fun td(init: kotlin_lang.kotlin_in_action._11_dsl.TD.() -> Unit) = doInit(kotlin_lang.kotlin_in_action._11_dsl.TD(), init)
}

class TD : kotlin_lang.kotlin_in_action._11_dsl.Tag("td") {
    fun p(init: kotlin_lang.kotlin_in_action._11_dsl.P.() -> Unit) = doInit(kotlin_lang.kotlin_in_action._11_dsl.P(), init)
}

class P : kotlin_lang.kotlin_in_action._11_dsl.Tag("p") {
    lateinit var text: String
    override fun toString(): String = "<$name>$text</$name>"
}

fun e3() {
    println(
        kotlin_lang.kotlin_in_action._11_dsl.table {
            // td {} <- тут td недоступна. Лямбды с ресивером задают правила разрешения имён
            // Контекст разрешения имён в каждом блоке определяется типом получателя
            repeat(2) {
                tr {
                    // При этом получатель внешнего лямбда-выражения останется доступным во вложенных (можно ограничить доступность аннотацией @DslMarker).
                    this@table.name
                    td { }
                }
            }


        }
    ) // <table><tr><td></td></tr><tr><td></td></tr></table>

    println(
        kotlin_lang.kotlin_in_action._11_dsl.table {
            tr {
                td {
                    p { text = "Hello World!" }
                }
            }
        }
    ) // <table><tr><td><p>Hello World!</p></td></tr></table>
}

/**
 * ## 11.3. Гибкое вложение блоков с использованием соглашения "invoke"
 * Соглашение invoke позволяет вызывать объекты как функции.
 *
 * Это же соглашение позволяет вызывать лямбды: lambda.invoke() -> lambda().
 * invoke - метод интерфейса FunctionN.
 */
class Greeter(val greeting: String) {
    operator fun invoke(name: String) {
        println("$greeting, $name!")
    }
}
fun e4() {
    val englishGreeter = kotlin_lang.kotlin_in_action._11_dsl.Greeter("Hi")
    englishGreeter("Mike") //<- вызов экземпляра Greeter как функции.
    // Hi, Mike!

    val predicate = kotlin_lang.kotlin_in_action._11_dsl.SomePredicate("projectX")
    listOf("a", "b", "projectX").filter(predicate) //<- передача экземпляра предиката
        .let { println(it) } // [projectX]
}

class SomePredicate(val project: String)
    : (String) -> Boolean { //Просто сокращённый синтаксис Function1
    override fun invoke(p1: String): Boolean {
        return project == p1
    }
}

/**
 * ## 11.3. Применение invoke в DSL.
 * Часто бывает нужно, чтобы API поддерживал и вложенные блоки (1) и простые последовательности вызовов (2):
 *
 *      dependencies {
 *          compile("junit:junit:4.11")
 *      }
 *
 *      dependencies.compile("junit:junit:4.11")
 *
 * Тут dependencies - это экземпляр класса DependencyHandler, который определяет методы compile и invoke (причём invoke принимает лямбду).
 */
class DependencyHandler {
    fun compile(coordinate: String) {
        println("Added dependency on $coordinate")
    }

    operator fun invoke(
        body: kotlin_lang.kotlin_in_action._11_dsl.DependencyHandler.() -> Unit
    ) {
        body()
    }
}

private fun e5() {
    val dependencies = kotlin_lang.kotlin_in_action._11_dsl.DependencyHandler()

    dependencies.compile("junit:junit:4.11")

    //Тк последний параметр invoke - это лямбда, можно открывать её скоуп сразу у объекта!
    dependencies {// this: DependencyHandler <- экземпляр выступает как неявный получатель.
        compile("junit:junit:5.11")
    } // Added dependency on junit:junit:5.11
}

/**
 * ## 11.4. DSL в Kotlin на практике.
 *
 * ## Цепочки инфиксных вызовов: "should" в фреймворках тестирования.
 */
infix fun <T> T.should(matcher: kotlin_lang.kotlin_in_action._11_dsl.Matcher<T>) = matcher.test(this)

interface Matcher<T> {
    fun test(value: T)
}

class startWith(val prefix: String) : kotlin_lang.kotlin_in_action._11_dsl.Matcher<String> {
    override fun test(value: String) {
        if (!value.startsWith(prefix)){
            throw AssertionError("String $value does not start with $prefix")
        }
    }
}

private fun e6() {
    "kotlin" should kotlin_lang.kotlin_in_action._11_dsl.startWith("kot")

    "kotlin" should kotlin_lang.kotlin_in_action._11_dsl.start with "kot"
    "kotlin" should kotlin_lang.kotlin_in_action._11_dsl.end with "in"
}

interface TestAction
object start : kotlin_lang.kotlin_in_action._11_dsl.TestAction
object end : kotlin_lang.kotlin_in_action._11_dsl.TestAction

infix fun String.should(action: kotlin_lang.kotlin_in_action._11_dsl.TestAction): kotlin_lang.kotlin_in_action._11_dsl.ActionWrapper {
    return when(action) {
        kotlin_lang.kotlin_in_action._11_dsl.start -> kotlin_lang.kotlin_in_action._11_dsl.StartWrapper(this)
        kotlin_lang.kotlin_in_action._11_dsl.end -> kotlin_lang.kotlin_in_action._11_dsl.EndWrapper(this)
        else -> error("Unknown test action: $action")
    }
}

interface ActionWrapper {
    infix fun with(value: String)
}
class StartWrapper(val value: String) : kotlin_lang.kotlin_in_action._11_dsl.ActionWrapper {
    override infix fun with(prefix: String) {
        if (!value.startsWith(prefix)) throw AssertionError("String $value does not start with $prefix")
    }
}

class EndWrapper(val value: String) : kotlin_lang.kotlin_in_action._11_dsl.ActionWrapper {
    override infix fun with(postfix: String) {
        if (!value.endsWith(postfix)) throw AssertionError("String $value does not end with $postfix")
    }
}

/**
 * Определение расширений для простых типов (примитивов).
 */
val Int.days: Period
    get() = Period.ofDays(this)

val Period.ago: LocalDate
    get() = LocalDate.now() - this

val Period.fromNow: LocalDate
    get() = LocalDate.now() + this

private fun e7() {
    val yesterday = 1.days.ago
    val tomorrow = 1.days.fromNow

    println("$yesterday -> $tomorrow") //2024-02-02 -> 2024-02-04
}

fun main() {
    kotlin_lang.kotlin_in_action._11_dsl.e1()
    kotlin_lang.kotlin_in_action._11_dsl.e2()
    kotlin_lang.kotlin_in_action._11_dsl.e3()
    kotlin_lang.kotlin_in_action._11_dsl.e4()
    kotlin_lang.kotlin_in_action._11_dsl.e5()
    kotlin_lang.kotlin_in_action._11_dsl.e6()
    kotlin_lang.kotlin_in_action._11_dsl.e7()
}