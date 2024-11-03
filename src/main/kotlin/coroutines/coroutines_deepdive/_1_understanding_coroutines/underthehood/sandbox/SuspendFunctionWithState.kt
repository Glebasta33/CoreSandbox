package coroutines.coroutines_deepdive._1_understanding_coroutines.underthehood.sandbox

import kotlinx.coroutines.delay

/**
 * Под капотом:
 * ```
 * public final class SuspendFunctionWithStateKt {
 *    @Nullable
 *    public static final Object myFunctionWithState(@NotNull Continuation $completion) {
 *       Object $continuation;
 *       label20: {
 *          if ($completion instanceof <undefinedtype>) {
 *             $continuation = (<undefinedtype>)$completion;
 *             if ((((<undefinedtype>)$continuation).label & Integer.MIN_VALUE) != 0) {
 *                ((<undefinedtype>)$continuation).label -= Integer.MIN_VALUE;
 *                break label20;
 *             }
 *          }
 *
 *          $continuation = new ContinuationImpl($completion) {
 *             int I$0;
 *             // $FF: synthetic field
 *             Object result;
 *             int label;
 *
 *             @Nullable
 *             public final Object invokeSuspend(@NotNull Object $result) {
 *                this.result = $result;
 *                this.label |= Integer.MIN_VALUE;
 *                return SuspendFunctionWithStateKt.myFunctionWithState((Continuation)this);
 *             }
 *          };
 *       }
 *
 *       Object $result = ((<undefinedtype>)$continuation).result;
 *       Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
 *       int counter; // Переменная.
 *       switch (((<undefinedtype>)$continuation).label) {
 *          case 0:
 *             ResultKt.throwOnFailure($result);
 *             String var6 = "Before";
 *             System.out.println(var6);
 *             counter = 0;
 *             ((<undefinedtype>)$continuation).I$0 = counter; // В поле объекта Continuation вставляются данные...
 *             ((<undefinedtype>)$continuation).label = 1; // .. эти данные будут использоваться на шаге 1!
 *             if (DelayKt.delay(1000L, (Continuation)$continuation) == var5) {
 *                return var5;
 *             }
 *             break;
 *          case 1:
 *             counter = ((<undefinedtype>)$continuation).I$0; // Получение данных из объекта Continuation
 *             ResultKt.throwOnFailure($result);
 *             break;
 *          default:
 *             throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
 *       }
 *
 *       ++counter;
 *       String var2 = "Counter: " + counter;
 *       System.out.println(var2);
 *       var2 = "After";
 *       System.out.println(var2);
 *       return Unit.INSTANCE;
 *    }
 * }
 * ```
 */
suspend fun myFunctionWithState() {
    println("Before")
    var counter = 0
    delay(1000)
    counter++
    println("Counter: $counter")
    println("After")
}
