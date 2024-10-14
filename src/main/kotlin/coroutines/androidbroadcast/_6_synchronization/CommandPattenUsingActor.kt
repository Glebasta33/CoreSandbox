package coroutines.androidbroadcast._6_synchronization

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Если нужно делать несколько типов операций над данными используется паттерн "Команда".
 *
 * Набор возможных команд при обращении к общему ресурсу.
 * Команды отправляются из корутин, а actor будет их последовательно обрабатывать.
 */
sealed class CounterCommand {
    class Add(val count: Int) : CounterCommand()
    class Remove(val count: Int) : CounterCommand()
    class Get(val response: CompletableDeferred<Int> = CompletableDeferred()) : CounterCommand()
}

class Counter(coroutineContext: CoroutineContext = EmptyCoroutineContext) {

    private val scope = CoroutineScope(coroutineContext)
    private var counter = 0

    @OptIn(ObsoleteCoroutinesApi::class)
    private val counterCommands = scope.actor<CounterCommand>(capacity = Channel.BUFFERED) {
        for (command in this) {
            when(command) {
                is CounterCommand.Add -> counter += command.count
                is CounterCommand.Remove -> counter -= command.count
                is CounterCommand.Get -> command.response.complete(counter)
            }
        }
    }

    suspend fun add(count: Int) {
        counterCommands.send(CounterCommand.Add(count))
    }

    suspend fun remove(count: Int) {
        counterCommands.send(CounterCommand.Remove(count))
    }

    suspend fun getCount(): Int {
        val getCommand = CounterCommand.Get()
        counterCommands.send(getCommand)
        return getCommand.response.await()
    }
}

fun main(): Unit = runBlocking {
    val counter = Counter(Dispatchers.Default)

    val jobs = List(200) {
        launch {
            repeat(10) {
                counter.add(1)
            }
        }
    }
    jobs.joinAll()
    println(counter.getCount()) // 2000

    val jobs2 = List(100) {
        launch {
            repeat(10) {
                counter.remove(1)
            }
        }
    }
    jobs2.joinAll()
    println(counter.getCount()) // 1000
}