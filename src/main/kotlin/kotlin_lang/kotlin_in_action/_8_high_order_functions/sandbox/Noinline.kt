package kotlin_lang.kotlin_in_action._8_high_order_functions.sandbox

fun main() {
    bar(
        inlined = {
            println("INLINE")
        },
        notInlined = {
            println("NO INLINE")
        }
    )
}

private inline fun bar(inlined: () -> Unit, noinline notInlined: () -> Unit) {
    inlined()
//    Foo(inlined) <- error: Illegal usage of inline-parameter
    // Тк лябда-параметр не вызывается внутри inline-функции, а сохраняется для последующего использования, его невозможно встроить,
    // потому что должен существовать объект (анонимного класса), содержащий этот код в своём методе

    // подобные лямбды нужно помечать noinline

    Foo(notInlined)
}

private class Foo(private val notInlined: () -> Unit)