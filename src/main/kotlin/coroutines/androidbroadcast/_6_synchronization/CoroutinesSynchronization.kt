package coroutines.androidbroadcast._6_synchronization

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * ## 6. Синхронизация между корутинами.
 * Корутины могут выполняться параллельно на нескольких потоках, когда они используют Dispatcher, поддерживающий многопоточность (например, Dispatchers.Default).
 * В такой ситуации может возникать доступ к одному ресурсу из нескольких корутин.
 *
 *     var counter = 0
 *     launch(Dispatchers.Default) { counter++ }
 *     launch(Dispatchers.Default) { counter++ }
 *
 * Чтобы такой код работал корректно, необходимо позаботиться о синхронизации доступа к общему объекту.
 *
 * Принципы синхронизации.
 * Все подходы к синхронизации доступа к общему ресурсу между несколькими потоками всегда основано на том, чтобы:
 * - только 1 поток может иметь доступ к ресурсу в любой момент времени.
 * - это происходит за счёт ограничения доступа
 * - либо за счёт последовательного доступа, при котором обращение к ресурсу (критическая секция) организовано в одном потоке.
 *
 * Критическая секция - участок исполняемого кода, в котором производится доступ к общему ресурсу, который не должен быть одновременно использован более, чем одним потоком.
 *
 * ## Захват блокировки.
 * Синхронизация имеет одно важное правило: Снятие блокировки потока должно происходит на том же потоке, на котором они и была захвачена. (иначе поток зависнет навсегда).
 * Особенность корутин: после вызова suspend-функции корутина может быть продолжена в любом потоке в пуле диспатчера. Это значит, что в критической секции не должно быть вызовов suspend-функций!
 *
 *          val lock = ReentrantLock()
 *          launch(Dispatchers.Default) { lock.withLock { counter++ } }
 *          launch(Dispatchers.Default) { lock.withLock { counter++ } }
 *
 * Mutex - замена Lock и synchronized из java. Поведение Mutex аналогично, но внутри withLock можно вызывать suspend-функции.
 * Но одна и та же корутина не сможет попасть в критическую секцию, которую она же и захватила в блокировку, в отличие от Lock и synchronized из java (?).
 *
 *          mutex.withLock { counter++ }
 *
 * ## Последовательный доступ.
 * - Самый простой способ организации последовательного доступа - делать всё на одном потоке. Для этого можно создать
 * отдельный Dispatcher, который будет работать на отдельном потоке. Для этого есть стандартная функция:
 *
 *          val singleThreadContext = newSingleThreadContext("Counter")
 *          mutex.withLock { counter++ }
 *          coroutineContext.cancel() - важно не забывать закрывать поток.
 *
 * ## Channel для синхронизации.
 * Последовательное выполнение могут обеспечить каналы.
 * Суть подхода: использовать actor, который будет служить очередью для выполнения операций.
 * Несколько корутин (неважно в каких потоках они работают) отправляют актору данные, а актор будет последовательно обращаться к ресурсу.
 * Этот подход называется "Синхронизация через коммуникацию" (рекомендованный подход для синхронизации между корутинами).
 *
 *    val syncActor = actor<Int>(capacity = Channel.BUFFERED) {
 *         for (data in this) {
 *             counter += data
 *         }
 *     }
 *     val jobs = List(200) {
 *         launch {
 *             repeat(10) {
 *                 syncActor.send(1)
 *             }
 *         }
 *     }
 *
 * Если нужно делать несколько типов операций над данными, подход можно модифицировать под паттерн команда см. [CommandPattenUsingActor.kt].
 *
 * ## java.util.concurrent
 * Все кейсы выше можно было решить, используя AtomicInteger.
 * Но это не нативное решение для котлина и корутин, и не будет работать на KMM.
 */
var counter = 0
val lock = ReentrantLock()
val mutex = Mutex()
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
val singleThreadContext = newSingleThreadContext("Counter")

@OptIn(ObsoleteCoroutinesApi::class)
fun main(): Unit = runBlocking {
    // Почему нужно синхронизировать корутины
    val jobs = mutableListOf<Job>()
    repeat(1000) {
        launch(Dispatchers.Default) { counter++ }
            .also { jobs.add(it) }
    }
    repeat(1000) {
        launch(Dispatchers.Default) { counter++ }
            .also { jobs.add(it) }
    }

    jobs.joinAll()
    println(counter) // 1979, 1993, 1987... Хотя должно быть 2000

    //Синхронизированный доступ (через ReentrantLock)
    counter = 0
    val jobs2 = mutableListOf<Job>()
    repeat(1000) {
        launch(Dispatchers.Default) { lock.withLock { counter++ } }
            .also { jobs.add(it) }
    }
    repeat(1000) {
        launch(Dispatchers.Default) { lock.withLock { counter++ } }
            .also { jobs.add(it) }
    }

    jobs2.joinAll()
    println(counter) // 2000

    counter = 0
    val jobs3 = List(200) {
        launch {
            repeat(10) {
                withContext(singleThreadContext){ counter++ }
            }
        }
    }
    jobs3.joinAll()
    println(counter) // 2000

    // Синхронизация через Mutex
    counter = 0
    val jobs4 = List(200) {
        launch {
            repeat(10) {
                mutex.withLock { counter++ }
            }
        }
    }
    jobs4.joinAll()
//    coroutineContext.cancel() - JobCancellationException: BlockingCoroutine was cancelled
    println(counter) // 2000

    // Синхронизация через коммуникацию (через actor)
    counter = 0
    val syncActor = actor<Int>(capacity = Channel.BUFFERED) {
        for (data in this) {
            counter += data
        }
    }
    val jobs5 = List(200) {
        launch {
            repeat(10) {
                syncActor.send(1)
            }
        }
    }
    jobs5.joinAll()
    println(counter) // 2000
}