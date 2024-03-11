package kotlin_lang.kotlin_in_action._9_generics.sandbox

import java.io.Serializable

class Container <T>(vararg initial: T) where T : Any, T : Serializable {
    private val list = initial

    operator fun get(index: Int): T = list[index]
}

fun main() {
    val container = Container("cup", "bowl", "pen", "bottle")
    println(container[3])
}