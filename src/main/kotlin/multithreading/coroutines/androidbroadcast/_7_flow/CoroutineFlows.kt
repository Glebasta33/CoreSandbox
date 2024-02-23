package multithreading.coroutines.androidbroadcast._7_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

/**
 * ## 7. Kotlin Coroutines Flow. StateFlow. SharedFlow.
 * suspend-функции способны асинхронно возвращать только 1 значение.
 * Flow - это реактивный поток данных, который может возвращать несколько значение из одного источника.
 *
 * ## Cold Flow.
 * Flow - это холодный асинхронный поток данных, который последовательно эмитит значения, и заканчивается либо успешно, либо с ошибкой (в этом проявляется реактивность).
 * Flow - схож с Observable из RxJava (также имеет множество промежуточных и терминальных операторов).
 * Flow - холодный стрим данных аналогично Sequence. Код внутри Flow не запускается сразу, а будет запущен при вызове терминального оператора.
 * Горячий же стрим начинает работать сразу после создания.
 * Промежуточные операторы в Cold Flow - не suspend, терминальные - suspend.
 * Интерфейс Flow содержит 1 метод:
 *
 *          interface Flow<T> {
 *              suspend fun collect(collector: FlowCollector<T>)
 *          }
 *
 *          fun interface FlowCollector<in T> {
 *              suspend fun emit(value: T)
 *          }
 *
 * FlowCollector запускает процесс получения данных из Flow.
 *
 * ## Операторы через расширения.
 * Все операторы Flow (кроме collect) реализованы как extension-функции, что позволяет легко добавлять новые операторы.
 * (вообще такой подход рекомендуем в Kotlin: создать простейший интерфейс и дальше расширять его extension-функциями).
 *
 * ## Flow Builders.
 * Функция, которые создают Flow, называются Flow Builders.
 * flowOf(...), flow { ... }, ...
 *
 * ## Преобразование Flow.
 * Промежуточные операторы применяются к входящему Flow (upstream) и возвращают исходящий Flow (downstream):
 *
 *          Flow<T>.map(transform: suspend (value: T) -> R): Flow<R>
 *        (upstream)                                      (downstream)
 *
 * Самые популярные промежуточные операторы, такие же как и в коллекциях:
 * map, switchMap, combineLatest, debounce, sample, delayEach, filter, catch, flatMapConcat, flatMapMerge, flattenConcat... //TODO: Создать отдельную страницу и рассмотреть там работу каждого промежуточного оператора.
 * Любой промежуточный оператор работает примерно следующим образом: //TODO: Написать свой промежуточный оператор для преобразования Flow.
 * ```
 *      fun <T, R> Flow<T>.map(
 *          transform: suspend (value: T) -> R
 *      ): Flow<R> = flow { // 1. Создаёт исходящий Flow (downstream)
 *          collect { value -> // 2. Подписывается на входящий Flow (upstream)
 *              val newValue = transform(value) // 3. Получает данные из входящего Flow и производит манипуляции
 *              emit(newValue) // 4. Эмитит изменённые данные в исходящий Flow
 *          }
 *      }
 * ```
 * ## Запускаем сбор данных.
 * Все терминальные операторы - это suspend-функции, которые должны быть запушены внутри какого-то CoroutineScope, внутри корутины.
 * Вызов терминального оператора запускает изначальный входной поток и все промежуточные операторы.
 * Это происходит асинхронно и не будет блокировать поток (хотя вызов терминального оператора будет приостанавливать работу корутины, как это делают другие suspend-функции).
 * Примеры: collect, collectIndexed, first, fold, reduce, single, toList, toSet... // TODO Создать отдельную страницу и рассмотреть там работу каждого терминального оператора.
 * Чтобы не создавать каждый раз корутину для коллектинга данных из Flow, можно использовать оператор launchIn(scope: CoroutineScope), который создаст корутину под капотом.
 * ```
 *     flow {
 *         emit("hello")
 *     }
 *         .onEach { println(it) }
 *         .launchIn(CoroutineScope(Dispatchers.Default))
 *```
 *
 * ## Последовательное выполнение и буферизация.
 * collect - suspend-функция, которая как и все suspend-функции будет выполняться последовательно внутри корутины.
 * Вызов collect приводит к прерыванию корутины: после вызова каждого emit корутина будет дожидаться
 * выполнения всех промежуточных операторов и collect. (т.е. пока вся цепочка не отработает и не завершится вызов collect, новый emit не начнётся).
 * Таким образом Flow может выполняться очень долго (если, например, collect будет выполняться долго из-за записи данных в файл).
 * Поток данных (emit) может быть очень интенсивным, а их запись (collect) - медленной.
 * В таком случае можно ускорить работу Flow, кэшируя значения и не дожидаясь работы collect.
 * Терминальный оператор buffer собирает все заэмиченные значения и передаёт их в коллектору, когда тот будет готов их обработать.
 * Под капотом эмититься данные будут в одно корутине, а собираться в другой (для этого используются Channel).
 *
 *             .buffer(
 *                 capacity = Channel.BUFFERED,
 *                 onBufferOverflow = BufferOverflow.SUSPEND
 *             )
 *
 * ## Всё можно сделать Flow.
 * Если в приложении есть коллбеки или используется какой-либо реактивный подход - это можно превратить во Flow.
 * Есть специальный билдер callbackFlow, который с помощью каналов осуществляет коммуникацию между созданным Flow и callback style api. см. [CallbackToFlow.kt]
 * ProducerScope реализует SendChannel и поэтому можно вызвать функции типа send внутри callbackFlow
 *
 *          fun <T> callbackFlow(
 *              block: suspend ProducerScope<T>.() -> Unit
 *          ): Flow<T>
 *
 *          interface ProducerScope<in E> : CoroutineScope, SendChannel<E> {
 *               public val channel: SendChannel<E>
 *          }
 *
 * ## Смена контекста выполнения.
 * Flow имеет собственный контекст выполнения, который нельзя менять внутри операторов с помощью withContext - это выдаст ошибку.
 * По умолчанию Flow (emit) будет работать в том же контексте, где и вызов collect.
 * Чтобы сменить контекст выполнения Flow, нужно использовать оператор flowOn.
 * flowOn меняет контекст только для части цепочки выше.
 * Для collect контекст сохраняется такой же как в месте вызова (правило Context Preservation - сохранение контекста).
 * ```
 *          flow {
 * //        withContext(Dispatchers.IO) {}  IllegalStateException: Flow invariant is violated
 *             emit("Hello from: ${currentCoroutineContext()}")
 *     }.flowOn(Dispatchers.IO)
 * ```
 *
 * ## Обработка ошибок.
 * Тк collect - это обычная suspend-функция, можно обернуть её в try-catch:
 * ```
 *      try {
 *         flow<Int> {
 *             throw RuntimeException("error from flow")
 *         }.collect{}
 *     } catch (e: Exception) {
 *         println("try-catch exception: ${e.message}")
 *     }
 * ```
 * При обработке ошибок во Flow необходимо следовать правилу Exception Transparency (прозрачность исключений) - ошибки в Flow всегда должны доходить до коллектора.
 * Промежуточные операторы не должны обрабатывать исключения внутри, а должны пробрасывать их дальше.
 * Оператор catch можно использовать для перехвата исключений внутри цепочки (он нарушает Exception Transparency, но исключения из него можно пробрасывать дальше в downstream).
 */

fun main(): Unit = runBlocking {

    println("before")

    val flow = flow<Int> {
        repeat(3) {
            delay(500)
            emit(it)
        }
    }
    launch {
        flow
            .map { "Number square: ${it * it} - (from coroutine)" }
            .collect {
                println(it)
            }
    }

    flow
        .map { "Number square: ${it * it}" }
        .collect {
            println(it)
        }

    println("after")

    flow {
        emit("hello")
    }
        .onEach { println(it) }
        .launchIn(CoroutineScope(Dispatchers.Default))



    flowOf(1, 2, 3, 3, 3, 4, 4, 5, 5, 6, 7, 7, 7)
        .unique()
        .toList()
        .joinToString(separator = ", ")
        .let { println(it) } // 1, 2, 3, 4, 5, 6, 7


//    buffer()

    // Смена контекста выполнения Flow
    flow {
//        withContext(Dispatchers.IO) {} // IllegalStateException: Flow invariant is violated
            emit("Hello from: ${currentCoroutineContext()}")
    }
        .onEach { println("onEach 1: ${currentCoroutineContext()}") } // Dispatchers.IO
        .onEach { println("onEach 2: ${currentCoroutineContext()}") } // Dispatchers.IO
        .flowOn(Dispatchers.IO)
        .onEach { println("onEach 3: ${currentCoroutineContext()}") } // BlockingEventLoop@55f3ddb1
        .collect {
            println("$it. (Collected in ${currentCoroutineContext()})")
        }

    try {
        flow<Int> {
            throw RuntimeException("error from flow")
        }
            .catch { println("operator catch: ${it.message}")
                throw RuntimeException("error from flow")
            }
            .collect{}
    } catch (e: Exception) {
        println("try-catch exception: ${e.message}")
    }
}


//Буферизация
private suspend fun buffer() {
    measureTimeMillis {
        flow {
            repeat(1000) {
                emit(it)
            }
        }
            .buffer(
                capacity = Channel.BUFFERED,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
            .collect {
                delay(1)
            }
    }.let { println("${it / 1000} c") }
}

// Кастомный оператор, который эмитит только изменившееся значения.
fun <T> Flow<T>.unique(): Flow<T> = flow {
    var lastValue: Any? = NoValue
    collect { value: T ->
        if (value != lastValue) {
            lastValue = value
            emit(value)
        }
    }
}

private object NoValue