package multithreading.coroutines.androidbroadcast._channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.BlockingQueue

/**
 * ## 5. Каналы. Зачем если есть Flow?
 * Как передавать значения между корутинами?
 * Можно использовать async + await, но они позволяют работать только с одним значением.
 * Для передачи нескольких значений между корутинами используются Channel и Flow.
 *
 * Что такое Channel?
 * Channel - это аналог [BlockingQueue] из Java, но построенный на основе корутин и прерывания.
 * 2 основных метода Channel (suspend-функции):
 * - send(element: E) - отправить значение в канал.
 * - receive(): E - получить значение из канала.
 * (Получить все значения из канала можно также пройдя по нему в цикле - for (value in channel)).
 * Также есть не suspend аналоги send и receive:
 * - trySend
 * - tryReceive
 * (Но будет работать только с Channel.BUFFERED)
 *
 * Закрываем канал.
 * По умолчанию канал будет работать пока не будет закрыт с помощью вызова функции Channel.close(cause: Throwable? = null): Boolean
 * При попытке считать значение из закрытого канала будет выброшено исключение - ClosedReceiveChannelException.
 * Можно проверить доступен ли канал для отправки или получения данных через поля Channel.isClosedForSend и Channel.isClosedForReceive.
 *
 * Одновременно у канала может быть несколько отправителей и получателей.
 *
 * Channel может приводить к приостановке корутины пока он не обработает значение (на отправку или на получение).
 * Чтобы этого не происходило Channel, после того как он перестал быть нужен, нужно обязательно закрывать close() и обрабатывать потенциальную ошибку через try-catch:
 *
 *          //TODO: Разобраться как обрабатывать ошибки из Channel. Как правильно и (в какой момент закрывать Channel)?
 *         try {
 *             notClosedChannel.send("notClosedChannel")
 *         } catch (e: ClosedSendChannelException) {
 *             println(e.message)
 *         }
 *
 * Раньше у Channel были операторы.
 * В первых версиях библиотеки каналы имели множество операторов для модификации потока данных, но с появлением Flow операторы были убраны,
 * а у Channel осталась единственная цель - коммуникация между несколькими корутинами.
 * Но можно конвертировать Channel во Flow:
 * // Можно вызвать только 1 collect - 1 получать
 * private val flow1: Flow<Int> = channelRendezvous.consumeAsFlow()
 * // Можно вызвать несколько collect
 * private val flow2: Flow<Int> = channelRendezvous.receiveAsFlow()
 *
 * Channel без буфера.
 * По умолчанию у каналов нет буффера.
 * При каждом вызове send отправляющая корутина будет приостановлена, пока не отработает receive в другой корутине (должно как бы состояться свидание - "рандеву").
 * При создании канала можно указать значение параметра capacity, которое отвечает за размер буфера.
 * При capacity RENDEZVOUS должен быть обязательно 1 отправитель и получатель.
 * При capacity BUFFERED можно вызывать send даже без наличия получателей - значение сохранится в буфер и отправится позже, когда получатель появится.
 *
 * Стандартные размеры буфера из констант Channel: //TODO: Проверить особенности работы каждого типа
 * - RENDEZVOUS - без буфера.
 * - CONFLATED - размер буфера = 1. Хранит только последнее полученное значение, предыдущее удаляется.
 * - BUFFERED - стандартный размер буфера, который определён в настройках окружения (по умолчанию 64).
 * - UNLIMITED - Максимально возможный размер (Int.MAX_SIZE).
 * - Также можно задать вручную размер через Int.
 *
 *
 *      fun <E> Channel(
 *          //Размер буфера
 *          capacity: Int = RENDEZVOUS,
 *
 *          // Что делать при переполнении буфера
 *          onBufferOverflow: BufferOverflow = SUSPEND,
 *
 *          // Сюда попадут все значения, которые не доставили
 *          onUndeliveredElement: ((E) -> Unit)? = null
 *      ): Channel<E>
 *
 * Политика поведения при переполнении буфера.
 * - SUSPEND - отправляющая корутина будет приостановлена, если буфер переполнен и нет получателя, чтобы обработать (по умолчанию).
 * - DROP_OLDEST, DROP_LATEST - удаляет значения в буфере при переполнении. send() не приостанавливает корутину.
 *
 * actor & produce.
 * actor & produce - это билдеры, которые возвращают ReceiveChannel и SendChannel.
 * produce отправляет данные изнутри, actor - извне.
 *
 *
 *     val receiveChannel: ReceiveChannel<String> = produce {
 *         send("from inside")
 *     }
 *     launch {
 *         val data = receiveChannel.receive()
 *         println("data: $data")
 *     }
 *
 *     val actorChannel: SendChannel<Any> = actor {
 *         val data = receive()
 *         println("data: $data")
 *     }
 *     launch {
 *         actorChannel.send("from outside")
 *     }
 *
 * actor помогает для синхронизации работы корутин (next lessons).
 *
 * Channel vs Flow.
 * Каналы позволяют получать и передавать данные извне.
 * Каналы дают возможность выстраивать связь и передавать данные между несколькими корутинами одновременно. //TODO: Реализовать кейс с передачей данных между большим количеством корутин.
 * Flow же - это скорее просто поток данных...
 */

val channelRendezvous: Channel<Int> = Channel(Channel.RENDEZVOUS)
val channelBuffered: Channel<Int> = Channel(Channel.BUFFERED)

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
fun main(): Unit = runBlocking {

    repeat(2) {
        launch {
            repeat(5) {
                delay(1000)
                if (it == 3) channelRendezvous.close()
                if (channelRendezvous.isClosedForSend.not()) {
                    channelRendezvous.send(it)
                }
            }
        }
    }


    launch {
        for (value in channelRendezvous) {
            println(value)
        }
    }

    sendNotSuspend()
    receiveNotSuspend()

    val notClosedChannel: Channel<Any> = Channel()

    val job = launch {
        try {
            notClosedChannel.send("notClosedChannel")
        } catch (e: ClosedSendChannelException) {
            println(e.message)
        }
    }

    launch {
        repeat(1) {
            delay(1000)
            println(job.isActive)
            try {
                println(notClosedChannel.receive())
            } catch (e: ClosedSendChannelException) {
                println(e.message)
            }
        }
    }



    launch {
        flow1
            .map { "flow1: $it" }
            .collect {
                println(it)
            }
    }

    launch {
        flow2
            .map { "flow2: $it" }
            .collect {
                println(it)
            }
    }

    launch {
        flow2
            .map { "flow2: $it" }
            .collect {
                println(it)
            }
    }

    val receiveChannel: ReceiveChannel<String> = produce {
        send("from inside")
    }
    launch {
        val data = receiveChannel.receive()
        println("data: $data")
    }

    val actorChannel: SendChannel<Any> = actor {
        val data = receive()
        println("data: $data")
    }
    launch {
        actorChannel.send("from outside")
    }
}

// Можно вызвать только 1 collect - 1 получать
private val flow1: Flow<Int> = channelRendezvous.consumeAsFlow()
// Можно вызвать несколько collect
private val flow2: Flow<Int> = channelRendezvous.receiveAsFlow()


private fun sendNotSuspend() {
    val result: ChannelResult<*> = channelBuffered.trySend(99)
    when {
        result.isSuccess -> println("sent successfully")
        result.isFailure -> println("failure: ${result.exceptionOrNull()}")
        result.isClosed -> println("closed")
    }
}

private fun receiveNotSuspend() {
    val result: ChannelResult<Int> = channelBuffered.tryReceive()
    when {
        result.isSuccess -> println("received successfully: ${result.getOrNull()}")
        result.isFailure -> println("failure: ${result.exceptionOrNull()}")
        result.isClosed -> println("closed")
    }
}