package coroutines.coroutines_deepdive._3_channel_and_flow.hot_vs_cold

/**
 * Все источники данных можно поделить на 2 типа: горячие и холодные
 *
 * | Hot      | Cold |
 * | -------- | ------- |
 * | Collections  | Sequence, Stream    |
 * | Channel | Flow, RxJava     |
 * | Производят элементы независимо от их потребления, выполняют хранение элементов  | Ленивые, выполняют операции по требованию, ничего не хранят.   |
 * | Операции над горячими источниками данных стартуют немедленно (билдеры) | Операции над холодными источниками данных не стартуют, пока элементы не потребуются потребителем |
 * |        | Могут быть бесконечными      |
 * |        | Делают минимальное количество операций      |
 * |        |  Используют меньше памяти     |
 * | Хранят элементы в памяти  | Являются лишь определением того, как вычислять элементы |
 * | Не нужно повторно производить вычисления  | Вычисления выполняются каждый раз заново при запросе элемента |
 */
fun main() {
    // Hot:
    val list = buildList {
        repeat(3) {
            add("User$it")
            println("List: Added User")
        }
    }

    val list2 = list.map {
        println("List: Processing")
        "Processed $it"
    }

    // Cold:
    val sequence = sequence {
        repeat(3) {
            yield("User$it")
            println("Sequence: Added User")
        }
    }

    val sequence2 = sequence.map {
        println("Sequence: Processing")
        "Processed $it"
    }
}