package multithreading.coroutines.androidbroadcast._4_exception_handling

import kotlinx.coroutines.*

/**
 * ## 4. Обработка ошибок в корутинах
 *
 * try-catch.
 * Внутри корутины можно использовать try-catch для обработки исключений.
 * suspend-функции можно оборачивать в try-catch.
 *
 *      private suspend fun doSomethingLong()
 *
 *      try {
 *         doSomethingLong()
 *      } catch (e: Exception) {
 *         println(e.message)
 *      }
 *
 * launch и async надо обрабатывать по-разному, тк корутин-билдеры делятся на 2 типа относительно
 * работы с исключениями:
 * launch и actor - самостоятельная обработка исключений (внутри корутины).
 * async и produce - требуют обработки исключений от того, кто вызывает код.
 *
 *     val deferred: Deferred<String> = async {
 *         getResult()
 *     }
 *
 *     try {
 *         deferred.await()
 *     } catch (e: Exception) {
 *         println(e.message)
 *     }
 *
 * Все необработанные исключения будут доставляться родительской корутине или скоупу, и приводить к крашу, если не обработать.
 * Помимо SupervisorJob есть подход, чтобы отлавливать исключения в родительской корутине (скоупе).
 * coroutineScope (+try-catch) "перехватывает" исключения:
 *
 *     try {
 *         coroutineScope {
 *             doSomethingLong()
 *         }
 *     } catch (e: Exception) {
 *         println(e.message)
 *     }
 *
 * Что не так с async-await? (см. выше - то был пример из устаревшей забагованной документации).
 *
 *      supervisorScope {
 *         val deferred: Deferred<String> = async {
 *             getResult()
 *         }
 *
 *         try {
 *             deferred.await()
 *         } catch (e: Exception) {
 *             println(e.message)
 *         }
 *     }
 *
 * Подписка на результат Job.
 * Можно подписаться на уведомление о выполнении корутины. Но этот способ не перехватит исключение:
 *
 *     val job = launch(start = CoroutineStart.LAZY) {
 *         doSomethingLong()
 *     }
 *     job.invokeOnCompletion { cause: Throwable? ->
 *         if (cause != null) {
 *             println("success")
 *         } else {
 *             println("failure")
 *         }
 *     }
 *     job.join()
 *
 * Отмена - это тоже ошибка. //TODO: Протестить отмену корутин в Structured Concurrency + сравнить с простыми исключениями
 * Остановка корутины происходит с помощью специального исключения - CancellationException.
 * Вызов cancel() у job или scope создаёт и выбрасывает экземпляр CancellationException.
 * Но его обработка происходит иначе - и этот код зашит в корутины и не может быть изменён.
 * Отмена job не приведёт к отмене её родителя, но дочерние корутины будут остановлены.
 * Поэтому при обработке через try-catch в корутинах нужно пробрасывать CancellationException далее. //TODO: Написать свою реализацию cancellableCatching
 *
 * CoroutineExceptionHandler.
 * CoroutineExceptionHandler позволяет определить поведение для всех необработанных исключений,
 * которые происходят в текущем контексте выполнения корутин.
 * Выполняется в последнюю очередь, не выполняется при CancellationException. //TODO: Проверить как отрабатывает CoroutineExceptionHandler при CancellationException
 *
 * Выполнение finally.
 * В случае, если корутина завершается с ошибкой или при отмене через cancel(), то выполнить в ней
 * какую-либо suspend-функцию не получится. А иногда это нужно - например, в блоке finally.
 * Специально для это сделали особую версию Job - NonCancellable.
 *
 *         } finally {
 *             withContext(NonCancellable) { //TODO: Придумать и реализовать кейс с использованием NonCancellable
 *                 finally()
 *             }
 *         }
 *
 * NonCancellable можно использовать только с withContext!
 * В других местах NonCancellable приведёт к нарушению Structured Concurrency.
 */
fun main(): Unit = runBlocking {
    try {
        doSomethingLong()
    } catch (e: Exception) {
        println(e.message)
    }

//    val deferred: Deferred<String> = async {
//        getResult()
//    }
//
//    try {
//        deferred.await()
//    } catch (e: Exception) {
//        println(e.message)
//    }

    supervisorScope {
        val deferred: Deferred<String> = async {
            getResult()
        }

        try {
            deferred.await()
        } catch (e: Exception) {
            println(e.message)
        }
    }


    try {
        coroutineScope {
            doSomethingLong()
        }
    } catch (e: Exception) {
        println(e.message)
    }

//    val job = launch(start = CoroutineStart.LAZY) {
//        doSomethingLong()
//    }
//    job.invokeOnCompletion { cause: Throwable? ->
//        if (cause != null) {
//            println("success")
//        } else {
//            println("failure")
//        }
//    }
//    job.join()

    launch {
        try {
            doSomethingLong()
        } catch (e: Exception) {
            println(e.message)
        } finally {
            withContext(NonCancellable) {
                finally()
            }
        }
    }
}

private suspend fun doSomethingLong() {
    delay(1000)
    throw RuntimeException("doSomethingLong exception")
}

private suspend fun getResult(throwException: Boolean = true): String {
    delay(1000)
    return if (throwException) {
        throw RuntimeException("getResult exception")
    } else {
        "Result"
    }
}

private suspend fun finally() {
    println("finally")
}