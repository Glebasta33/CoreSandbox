package coroutines.androidbroadcast._7_flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * # Hot Flow.
 * Flow - это холодный стрим данных. Но если нам нужен горячий стрим (вроде LiveData или RxJava Subject).
 * ## SharedFlow.
 * SharedFlow - это горячий стрим данных, который эмитит значения всем его подписчикам.
 * Особенности SharedFlow:
 * - Бесконечный стрим данных (никогда не останавливается). Например, при попытке вызвать collect корутина уже никогда не закончится.
 * - Эмитит значения сразу после создания (горячий). Его экземпляр существует независимо от его подписчиков (в отличие от Cold Flow).
 * - Один экземпляр SharedFlow может содержать множество подписчиков.
 * - Не имеет контекста выполнения (flowOn не сработает).
 * - SharedFlow (и StateFlow) поделены на мутабельный и иммутабельный варианты (как это сделано с коллекциями в Kotlin).
 * ```
 *      interface SharedFlow<out T> : Flow<T> {
 *          public val replayCache: List<T>
 *          override suspend fun collect(collector: FlowCollector<T>): Nothing
 *      }
 *
 *      interface MutableSharedFlow<T> : SharedFlow<T>, FlowCollector<T> {
 *          override suspend fun emit(value: T)
 *          public fun tryEmit(value: T): Boolean
 *          public val subscriptionCount: StateFlow<Int>
 *          public fun resetReplayCache()
 *      }
 *
 *     public fun <T> MutableSharedFlow(
 *          replay: Int = 0,
 *          extraBufferCapacity: Int = 0,
 *          onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
 *      ): MutableSharedFlow<T>
 *
 * ## Буферизация в SharedFlow.
 * По умолчанию SharedFlow не сохраняет каких-либо значений, а просто передаёт данные из эмиттора подписчикам.
 * В такой конфигурации каждый вызов emit будет приводить к прерыванию корутины, пока подписчики не смогут принять значение.
 * Но в SharedFlow есть опции, которые позволяют гибко настраивать буферизацию.
 * Буфер SharedFlow состоит из 2-х частей:
 * - Replay Cache - последние заэмиченные значения (размер задаётся параметром replay: Int)
 *    Также можно получить все значения (replayCache: List<T>) или сбросить их (resetReplayCache).
 * - Extra Buffer - обычный буфер, который позволяет не суспендить корутину при вызове emit пока в него можно поместить новое значение.
 *    В этом буфере элементы сохраняются при наличии подписчиков, которые пока не могут обработать значения.
 *    (размер задаётся параметром extraBufferCapacity: Int).
 *    Также можно управлять поведением буфера при его переполнении (параметр onBufferOverflow: BufferOverflow, который принимает константы как в Channel).
 *
 * ## StateFlow.
 * StateFlow - это "частный случай" (наследник) SharedFlow.
 * StateFlow - специальный SharedFlow, который хранит одно значение и доставляет его всем своим подписчикам.
 * Новое значение будет доставляться подписчикам, только если оно изменилось.
 * StateFlow аналогичен SharedFlow со следующими параметрами:
 * ```
 *          MutableStateFlow ~ MutableSharedFlow(
 *  *          replay: Int = 1,
 *  *          extraBufferCapacity: Int = 0,
 *  *          onBufferOverflow: BufferOverflow = BufferOverflow.DROP_OLDEST
 *  *      )
 *
 *          interface StateFlow<out T> : SharedFlow<T> {
 *              public val value: T // текущее значение
 *          }
 *
 *          interface MutableStateFlow<T> : StateFlow<T>, MutableSharedFlow<T> {
 *              public override var value: T
 *              public fun compareAndSet(expect: T, update: T): Boolean //сравнение нового и старого значения
 *          }
 * ```
 * StateFlow чаще всего используется как замена LiveData в UI.
 */

val mutableSharedFlow = MutableSharedFlow<Int>()
val mutableStateFlow = MutableStateFlow(value = 0)

fun main(): Unit = runBlocking {

    //SharedFlow
    launch {// Эммитер
        delay(1000)
        mutableSharedFlow.emit(2)
    }

    launch {// Коллектор 1
        mutableSharedFlow.asSharedFlow()
            .map { it * it }
            .collect { println("sharedFlow.collect: $it") }
        println("finish") // unreachable code
    }
    launch {// Коллектор 2
        mutableSharedFlow.asSharedFlow()
            .map { it * it }
            .collect { println("sharedFlow.collect: $it") }
        println("finish") // unreachable code
    }


    //StateFlow
    launch {
        mutableStateFlow.asStateFlow().collect { println("stateFlow.collect: $it") }
    }

    launch {
        delay(1000)
        mutableStateFlow.update { oldValue ->
            oldValue + 1
        }
        delay(1000)
        mutableStateFlow.value = 3
    }
}