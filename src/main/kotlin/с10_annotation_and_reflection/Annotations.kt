package с10_annotation_and_reflection

/**
 * # 10. Аннотации и механизм рефлексии.
 * Аннотации и механизм рефлексии позволяют писать код, способный работать с неизвестными заранее произвольными классами.
 * С помощью аннотаций можно присвоить классам особую семантику, а механизм рефлексии позволит исследовать структуру классов
 * во время выполнения.
 * Синтаксис использования аннотаций в Kotlin такой же, как и в Java, но синтаксис объявления несколько отличается.
 * Механизм рефлексии похож на Java, но немного отличается в деталях.
 *
 * ## 10.1. Объявление и применение аннотаций.
 * Аннотация позволяет связать дополнительные метаданные с объявлением.
 * Аннотации могут иметь параметры только определенных типов: простые типы, строки, enum, ссылки на классы, классы других аннотаций и их массивы.
 * - Чтобы передать в аргументе класс, нужно добавить "::class" после имени класса - @MyAnnotation(MyClass::class)
 * - Чтобы предать в аргументе другую аннотацию, перед её именем не нужно добавлять @ (ReplaceWith - аннотация).
 * - Чтобы передать в аргументе массив, используй функцию arrayOf (@RequestMapping(path = arrayOf("/foo", "/bar")).
 */
private fun e1() {
    @Deprecated("Use removeAt(index) instead", ReplaceWith("removeAt(index)"))
    fun remove(index: Int) {}

8
    remove(2) // IDE предлагает автоматическую замену на removeAt
}

/**
 * ## Целевые элементы аннотаций.
 * Часто одному объявлению в коде на Kotlin соответствует несколько объявлений на Java, каждое из которых может быть целью аннотации.
 * Например, свойству (property) в Kotlin соответствует поле, методы доступа, параметр в Java.
 * Аннотируемы элемент можно указать с помощью объявления цели.
 *
 *      class TempFolderTest {
 *          @get:Rule //<- get - цель, Rule - имя аннотации.
 *          val folder = TemporaryFolder()
 *          // В Java - само поле приватно, нужно обращаться к публичному геттеру.
 *      }
 *
 * ## Использование аннотация для сериализации JSON
 * Аннотации часто используются в фреймворках и библиотеках, например, для настройки сериализации JSON.
 * 
 */


fun main() {
}