package multithreading.coroutines.androidbroadcast._3_coroutine_lifecycle

import kotlinx.coroutines.*

/**
 * ## 3. Жизненный цикл корутины. CoroutineScope. Structured Concurrency
 *
 * Жизненный цикл.
 * Любая асинхронная операция должна быть остановлена, когда результат её выполнения больше не нужен, чтобы не задействовать ресурсы устройства без необходимости.
 * Например, когда пользователь уходит с экрана - данные уже не нужны.
 * В потоках есть - interrupt(), в RxJava - disposable - но это не удобный API.
 * В корутинах же для этих целей сделали CoroutineScope - жизненный цикл для выполнения асинхронных операций.
 * Все корутины должны быть привязаны к какому-то CoroutineScope. Именно поэтому все билдеры корутин (launch и async) являются расширениями CoroutineScope:
 *
 *       CoroutineScope.launch {  }
 *       CoroutineScope.async {  }
 *
 * Исключение - runBlocking (мост между блокирующим подходом и подходом с прерыванием).
 *
 * Structured concurrency.
 * Structured concurrency - механизм, который представляет иерархическую структуру для организации работы корутин.
 * Все принципы Structured concurrency строятся на основе CoroutineScope и отношении "родитель-ребёнок" у Job.
 * Принцип работы CoroutineScope:
 * 1. Отмена Scope.
 * Scope может отменить выполнение всех дочерних корутин, если возникнет ошибка или операция будет отменена.
 * 2. Scope знает про все корутины.
 * Любая корутина, запускаемая в скоупе, будет храниться ссылкой в нём через отношение "родитель-ребёнок" у Job.
 * 3. Я тебя буду ждать.
 * scope автоматически ожидает выполнения всех дочерних корутин, но не обязательно завершается вместе с ними.
 *
 * Scope vs Context.
 * CoroutineScope - обёртка над CoroutineContext.
 * Отличие между ними в целевом назначении:
 * CoroutineContext - набор параметров для выполнения корутины.
 * CoroutineScope - предназначен для объединения всех запущенных корутин в рамках него, а также под капотом добавляет
 * для всех внутренних корутин объединяющий родительских Job.
 *
 *      public interface CoroutineScope {
 *          public val coroutineContext: CoroutineContext
 *      }
 *
 * GlobalScope - специальный CoroutineScope, который не привязан к какой-либо Job. Все корутины, запущенные в рамках него,
 * будет работать до своей остановки или остановки процесса.
 * Использование GlobalScope может привести к утечкам памяти.
 * Лучше создавать свой корутин скоуп, привязанный к ЖЦ Application.
 *
 * Как создать CoroutineScope?
 * - Можно использовать функцию CoroutineScope(context: CoroutineContext)
 * Любой CoroutineScope должен иметь свой Job. Если не добавить его явно, он будет добавлен автоматически.
 * Часто полезно использовать SupervisorJob - тогда ошибки из любой дочерней корутины не приведут к отмене всех корутин в скоупе.
 * Не обязательно задавать Dispatcher для CoroutineScope, тк будет использован вариант по умолчанию - Dispatchers.Default.
 * Но лучше задавать, чтобы всё контролировать.
 *
 * - Также можно использовать suspend-функцию coroutineScope { }.
 * Она может быть полезна для запуска новой корутины внутри suspend-функции, чтобы выполнить задачу параллельно.
 * Для создания CoroutineScope функция coroutineScope возьмёт контекст из родителя и добавит к нему Job, который будет связан с внешним скоупом.
 * CoroutineScope, созданный функцией coroutineScope будет работать по следующим правилам:
 * 1. Если происходит креш внутри, то он пробрасывается наверх родительскому scope.
 * 2. Остановка родительского scope приведёт к остановке внутреннего.
 * 3. Функция coroutineScope приостановит выполнение корутины, до тех пор пока все корутины и весь код внутри не будет выполнен.
 * Функция supervisorScope - аналогична coroutineScope, но без каскадной остановки корутин.
 * Такие корутин скоупы не подойдут, чтобы выполнять операцию дольше, чем родительской скоуп (отправка сообщения на сервер). Лучше создавать свой скоуп.
 *
 * Scope внутри корутины.
 * Каждая корутина внутри себя передаёт корутин скоуп:
 *
 *      launch { this: CoroutineScope
 *      }
 * Этот скоуп формируется по следующим правилам:
 *  Берётся контекст родительского скоупа, к нему добавляются элемента контекста, заданные в параметра при запуске корутины + создаётся новый Job, который связывается с родительским.
 *  Это позволяет организовать иерархию корутин на основе связи между Job.
 *
 * Отмена CoroutineScope.
 * Любой CoroutineScope является активным с его старта. Чтобы уничтожить нужно явно вызвать функцию cancel(), которая остановит все запущенные корутины внутри скоупа.
 * Чтобы отменить все внутренние корутины без отмены скоупа:
 *
 *      this.coroutineContext[Job]?.cancel()
 *
 * Лёгкая смена контекста - withContext().
 * Рекомендуется во всех suspend-функциях явно указывать контекст:
 *
 *      suspend fun loadData() = withContext(Dispatchers.IO) {}
 *
 *
 */
val myAppScope = CoroutineScope(SupervisorJob())

private fun CoroutineScope.cancelChildrenOnly() {
    this.coroutineContext[Job]?.cancel(cause = null)
}

private suspend fun loadData() = withContext(Dispatchers.IO) {}


fun main(): Unit = runBlocking {
    println(myAppScope.coroutineContext.job)

    myAppScope.cancelChildrenOnly()
    println(myAppScope.coroutineContext.isActive)
    myFunWithInternalScope()
}

private suspend fun myFunWithInternalScope() {
    delay(1000)
    println("suspended 1")
//    launch
    coroutineScope {
        launch(Dispatchers.IO) {
            delay(3000)
            println("internal coroutineScope 1")
        }
        launch(Dispatchers.IO) {
            delay(3000)
            println("internal coroutineScope 2")
        }
    }
    delay(1000)
    println("suspended 2")
}