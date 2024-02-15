package kotlin_lang.kotlin_in_action._2_basics

/**
 * # Kotlin в действии.
 * ## 2.5 Основы Kotlin. Исключения
 */

/**
 * В Java обязательно помечать метод, бросающий исключение:
 *     private void throwException() throws Exception {
 *         throw new Exception();
 *     }
 * В Kotlin не нужно этого делать:
 */
private fun throwException(): Nothing = throw Exception("message")

/**
 * В Java констркуция "throws" необходима в сигнатуре метода, потому что есть деление на checked(контролируемые)- и unchecked-exceptions.
 * И все checked-exceptions необходимо обрабатывать явно, либо пометить, что функция может бросить исключение конструкцией "throws".
 *
 * Kotlin не делает различий между checked- и unchecked-exceptions. Можно обрабатывать или не обрабатывать любые исключения.
 */

/**
 *  "throw" является выражением:
 */
//val exception: Nothing = throw Exception("") // код после throw - unreachable!

/**
 * "try" является выражением:
 */
fun readNumber() {
    val number = try {
        Integer.parseInt(readlnOrNull())
    } catch (e: NumberFormatException) {
        println("not a number")
        return
    }
    println(number)
}

fun main() {
    repeat(3) {
        readNumber()
    }
}