package kotlin_lang.kotlin_in_action._8_high_order_functions.sandbox

fun main() {
    bar {
        return@bar // локальный возврат - только выйдет из лямбды
        println("unreacheable code")
    }

    barInline(
        foo = {
            return@barInline // локальный возврат - только выйдет из лямбды
            println("unreacheable code")
        },
        doo = {
            return // нелокальный возврат - остановит выполнение main
                // return встроен, тк это inline-функция
        }
    )
}

private fun bar(foo: () -> Unit) {
    println("bar start")
    foo()
    println("bar end")
}

private inline fun barInline(crossinline foo: () -> Unit,  doo: () -> Unit) {
    println("barInline start")
    foo()
    println("barInline middle")
    doo()
    println("barInline end")
}