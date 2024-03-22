package kotlin_lang.kotlin_in_action._8_high_order_functions.sandbox

fun main() {
    performAction {
        println("ACTION")
    }
    // в скомпилированном виде выглядит так:
    // performAction((Function0)null.INSTANCE); - создаётся экземпляр анонимного класса Function0

    performActionInline {
        println("ACTION")
    }
    // в скомпилированном виде выглядит так:
    //      String var1 = "Before action";
    //      System.out.println(var1);
    //      String var3 = "ACTION";
    //      System.out.println(var3);
    //      var1 = "After action";
    //      System.out.println(var1);
}

private fun performAction(action: () -> Unit) {
    println("Before action")
    action.invoke()
    println("After action")
}

private inline fun performActionInline(action: () -> Unit) {
    println("Before action")
    action.invoke()
    println("After action")
}