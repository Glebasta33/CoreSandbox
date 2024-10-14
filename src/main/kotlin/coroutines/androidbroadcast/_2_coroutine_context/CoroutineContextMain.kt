package coroutines.androidbroadcast._2_coroutine_context

import kotlinx.coroutines.*

/**
 * ## 2. CoroutineContext
 *
 * Каждая корутина выполняется в каком-то контексте. Контекст представлен классом CoroutineContext.
 * CoroutineContext ~ Map<Key<Element>, Element>
 * CoroutineContext напоминает мапу, у которой каждый ключ указывает на определённый тип конек ста.
 *
 *      public interface CoroutineContext {
 *          public operator fun <E : Element> get(key: Key<E>): E?
 *
 *          public operator fun plus(context: CoroutineContext): CoroutineContext =
 *          ...
 *
 * При сложении контекстов (например в launch(E1 + E2) контексты будут объеденины.
 * При добавлении нового элемента с ключём, который уже был в мапе, новое значение перезапишет старое.
 *
 * При этом каждый элемент тоже является контекстом:
 *
 *          public interface Element : CoroutineContext {
 *              public val key: Key<*>
 *
 *         public override operator fun <E : Element> get(key: Key<E>): E? =
 *             if (this.key == key) this as E else null
 *             ...
 *
 * Такое устройства контекста позволяет объединить несколько элементов контекста в единое целое,
 * а также переопределять некоторые составляющие контекста.
 *
 * Элементы CoroutineContext:
 * 1. Job (Deferred) - представляет выполняемую в фоне задачу.
 *
 *      val job: Job = launch { }
 *
 * С помощью Job можно управлять работой корутины, она имеет свой жизненный цикл.
 * Также на основе Job можно организовать иерархию "родитель-ребёнок" между корутинами.
 * ЖЦ Job состоит из 6 состояний: New, Active, Completing, Completed, Cancelling, Cancelled.
 * Каждая Job может иметь родителя, что является важной частью Structured Concurrency.
 * Особенности Structured Concurrency:
 * - Отмена Job приведёт к отмене всех её дочерних Job (также это работает в обратном порядке).
 * - SuperviserJob не будет отменять другие дочерние джобы, если получит сообщение об отмене от одной из дочерних.
 *
 * Интерфейс Job прост и содержит несколько методов:
 *
 * cancel(cause: CancellationException? = null) - отменяет выполнение джобы и принимает опциональную причину отмены.
 * invokeOnCompletion(handler: CompletionHandler) - вызовется по окончании работы джобы.
 * join() - приостанавливает выполнения корутины и дожидается завершения джобы.
 * start() - запуск.
 * children - получение дочерних джоб.
 * ensureActive() - проверяет активна ли текущая Job и если нет выбросит CancellationException.
 *
 * 2. Dispatcher - отвечает на каком потоке(-ах) будет выполняться корутина.
 * Стандартные Dispatchers:
 * - Default - используется по умолчанию. Кол-во потоков в пуле соответсвет кол-ву ядер процессора (но не меньше 2).
 * - IO - Не менее 64 потоков в пуле.
 * - Main - главный поток. По умолчанию не определён, нужна специальная зависимость (например, coroutines-android).
 * - Unconfined - не привязан к какому-либо потоку. Выполнение корутины начинается в том же потоке, в котором происходит её создание и запуск.
 * А после вызова первой suspend-функции корутина продолжит выполнение в контексте этой suspend-функции.
 *
 * Также можно создать диспатчер самостоятельно, но лучше и пользовать стандартные реализации.
 *
 * Dispatchers не стоит использовать напрямую в коде. Официальная рекомендация создать обёртку и инджектить через DI.
 * Чтобы затем можно было легко подменить диспатчеры и их конфигурацию + подменять в тестах.
 *
 * 3. CoroutineExceptionHandler - обработчик исключений, которые происходят в корутинах.
 * Когда исключение перехватывается CoroutineExceptionHandler, дальше в родительскую Job исключение пробрасываться не будет.
 *
 * 4. CoroutineName - задаёт осмысленное имя для корутин. Полезно при отладке, в логах.
 */

val dispatcher = Dispatchers.Unconfined
val coroutineName = CoroutineName("My coroutine")
val myJob = Job()
val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    println(
        """
            ${coroutineContext[dispatcher.key]}
            ${coroutineContext[coroutineName.key]}
            ${coroutineContext[myJob.key]}
        """.trimIndent()
        //Dispatchers.Unconfined
        //CoroutineName(My coroutine)
        //StandaloneCoroutine{Cancelling}
    )
}

fun main(): Unit = runBlocking {

    val job: Job = launch(
        dispatcher + coroutineName + myJob + exceptionHandler
    ) {
        throw RuntimeException()
    }

     job.invokeOnCompletion {
         println("${job.isCompleted}") //true
     }

    job.join()
    println("finish")
}