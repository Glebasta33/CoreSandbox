package kotlin_lang.kotlin_in_action._8_high_order_functions.sandbox

fun main() {
    printNumber(100) // в байт-коде тут будет тело функции

    performAction { // в байт-коде тут будет тело функции c телом лямбды внутри
        println("Perform action")
    }

    val noCallSiteLambda = { println("Perform action from no call site") }
    performAction(noCallSiteLambda) // в байт-коде тут будет тело функции c noCallSiteLambda.invoke() внутри

    performNoInlineAction { // в байт-коде тут будет тело функции c action.invoke() внутри
        println("Perform noinline action")
    }
}

private inline fun printNumber(input: Number) {
    val text = "Number $input"
    println(text)
}

private inline fun performAction(action: () -> Unit) {
    println("Before action")
    action.invoke()
    println("After action")
}

private inline fun performNoInlineAction(noinline action: () -> Unit) {
    println("Before noinline action")
    action.invoke()
    println("After noinline action")
}