package coroutines.coroutines_deepdive._2_library.context

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

fun main() {
    val name: CoroutineName = CoroutineName("The Name")
    val element: CoroutineContext.Element = name
    val context: CoroutineContext = element

    val job: Job = Job()
    val jobElement: CoroutineContext.Element = job
    val jobContext: CoroutineContext = jobElement

    val foundName: CoroutineName? = context[CoroutineName/*.Key*/] // key - companion object
    val foundJob: Job? = jobContext[Job] // key - companion object
    // Это фича котлина: имя класса - это также ссылка на его companion object!

    println("Found: $foundName, $foundJob")

    val mergedCtx = context + jobContext
    println("Merged: ${mergedCtx[CoroutineName]}, ${mergedCtx[Job]?.isActive}")

    val mergedCtxUpd = mergedCtx + CoroutineName("Name 2") // Context element overriding
    println("Updated: ${mergedCtxUpd[CoroutineName]}")

    val deletedCtx = mergedCtxUpd.minusKey(CoroutineName)
    println("Deleted: ${deletedCtx[CoroutineName]}")

    val ctx = CoroutineName("Name 3") + Job()

    ctx.fold("For each: ") { accumulator, element -> "$accumulator -> $element" }
        .also(::println)


}