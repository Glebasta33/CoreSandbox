package kotlin_lang.kotlin_in_action._9_generics.sandbox

import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

private inline fun <reified T : Any> exploreType(value: T) {
    value.javaClass.kotlin.memberProperties.forEach {
        println("${it.name} = ${it.get(value)}")
    }
    value.javaClass.kotlin.functions.find { it.name == "hello" }?.call(value)
    println(value.javaClass.kotlin.visibility) // PUBLIC
}

data class Person(
    val name: String,
    val age: Int
) {
    fun hello() = println("Hello, my name is $name")
}

fun main() {
    val person = Person("John", 33)
    exploreType(person)
}