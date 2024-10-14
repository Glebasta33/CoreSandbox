package coroutines.coroutines_deepdive._1_understanding_coroutines

/**
 * # Coroutines: Deep Dive by Marcin Moskała.
 * # 1. Understanding Kotlin Coroutines.
 * Зачем вообще нужны корутины, если есть RxJava, да и сама Java поддерживает многопоточность?
 * Концепция корутин была описана ещё в 1963г, но ждала своего часа, когда индустрия будет готовой для реализации.
 * Корутины мультиплатформены, они не меняют структуру кода и проще для использования новичком.
 *
 * # Корутины в Android.
 * На фронтэнде часто нужно:
 * 1. Получить данные из 1 или нескольких источников.
 * 2. Обработать эти данные.
 * 3. Что-то сделать с этими данными (отобразить, сохранить в БД, ...).
 *
 * Описанную логику можно представить следующим образом, но в Android так нельзя сделать (нельзя было до появления корутин).
 * ```
 *      fun onCreate() {
 *          val news = getNewsFromApi()
 *          val sortedNews = news
 *          .sortedByDescending { it.publishedAt }
 *          view.showNews(sortedNews)
 *      }
 * ```
 *
 * Только MainThread может изменять UI, он очень важен и не может быть заблокирован.
 * Получение данных должно происходить в фоновом потоке, а затем передаваться в главный поток.
 *
 * Подход: Переключение потока
 * ```
 * fun onCreate() {
 *   thread {
 *       val news = getNewsFromApi()
 *       val sortedNews = news
 *           .sortedByDescending { it.publishedAt }
 *       runOnUiThread {
 *           view.showNews(sortedNews)
 *       }
 *   }
 * }
 * ```
 * Но такой подход не учитывает ЖЦ (не отменяет поток - утечки), ресурсозатратен, сложен для поддержки.
 *
 * Подход: Callbacks:
 * ```
 * fun onCreate() {
 *   getNewsFromApi { news ->
 *       val sortedNews = news
 *           .sortedByDescending { it.publishedAt }
 *       view.showNews(sortedNews)
 *   }
 * }
 * ```
 * Но если нужно сделать несколько вызовов последовательно, это подход приводит к Callback hell:
 * ```
 * fun showNews() {
 *   getConfigFromApi { config ->
 *       getNewsFromApi(config) { news ->
 *           getUserFromApi { user ->
 *               view.showNews(user, news)
 *           }
 *       }
 *   }
 * }
 * ```
 * Подход RxJava - сложен, требует сильной реорганизации кода под себя, нужно явно закрывать поток (dispose).
 *
 * ## Подход: Использование корутин.
 * Основная особенность корутин заключается в способности прерываться в определённой точке и возобновляться в будущем.
 * Благодаря этому мы можем вызывать код в главном потоке и приостанавливать его, пока не получим данные из API.
 * Когда корутина приостанавливается, поток не блокируется и свободен для других процессов (отрисовка UI, обработка кликов, ...)
 * ```
 * fun onCreate() {
 *   viewModelScope.launch {
 *       val news = getNewsFromApi()
 *       val sortedNews = news
 *           .sortedByDescending { it.publishedAt }
 *       view.showNews(sortedNews)
 *   }
 * }
 * ```
 */
fun main() {
    SequenceBuilder
}