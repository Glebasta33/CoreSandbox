package coroutines.androidbroadcast._7_flow.playground.intermediate_operators

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

/**
 * Flatten и flatMap - операторы, предназначенные для работы с вложенными коллекциями.
 * Слово "плоский" подразумевает перевод из "двумерного пространства" (вложенных коллекций) в "одномерное пространство" (единую коллекцию).
 */
fun main(): Unit = runBlocking {
    runFlattenWithCollections()
    runFlattenWithFlows()
}

private fun runFlattenWithCollections() {
    // flatten преобразует лист сетов Int в лист Int:
    val numberSets: List<Set<Int>> = listOf(setOf(1, 2, 3), setOf(4, 5, 6), setOf(1, 2))
    val numberList: List<Int> = numberSets.flatten()
    println(numberList) // [1, 2, 3, 4, 5, 6, 1, 2]

    // flatMap - это более гибкий вариант flatten,
    // flatMap = flatten (приобразут в единый список) + и map (модифицирует элементы).
    data class StringContainer(val values: List<String>)
    val containers = listOf(
        StringContainer(listOf("one", "two", "three")),
        StringContainer(listOf("four", "five", "six")),
        StringContainer(listOf("seven", "eight"))
    )
    println(containers.flatMap { it.values }) // [one, two, three, four, five, six, seven, eight]
}

private suspend fun runFlattenWithFlows() {
    val numberSetFlow = flow {
        emit(setOf(1, 2, 3))
        emit(setOf(4, 5, 6))
    }

    numberSetFlow
//        .flatten()
}