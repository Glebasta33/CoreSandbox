package coroutines.coroutines_deepdive._1_understanding_coroutines.underthehood.sandbox

import kotlinx.coroutines.delay
/**
 * Так эта suspend-функция выглядит под капотом:
 * ```
 * public final class SimpleSuspendFunctionKt {
 *    @Nullable
 *    public static final Object myFunction(@NotNull Continuation $completion) { // Добавляется параметр Continuation. Тип возвращаемого значения становится Object (тк во время прерывания функция возвращает специальный маркер COROUTINE_SUSPENDED : Any).
 *       Object $continuation; // suspend-функция нуждается в собственном объекте Continuation для хранения своего стейта.
 *       label20: {
 *          if ($completion instanceof <undefinedtype>) {
 *             $continuation = (<undefinedtype>)$completion;
 *             if ((((<undefinedtype>)$continuation).label & Integer.MIN_VALUE) != 0) {
 *                ((<undefinedtype>)$continuation).label -= Integer.MIN_VALUE;
 *                break label20;
 *             }
 *          }
 *
 *          $continuation = new ContinuationImpl($completion) { // Создаётся анонимный класс для Continuation suspend-функции и оборачивает параметр Continuation $completion. Но сначала идёт проверка, не обёрнута ли Continuation уже (см. выше).
 *             // $FF: synthetic field
 *             Object result; // Добавляется поле для хранения локального состояния.
 *             int label; // Добавляется поле с индексом текущего шага в стейт-машине.
 *
 *             @Nullable
 *             public final Object invokeSuspend(@NotNull Object $result) {
 *                this.result = $result;
 *                this.label |= Integer.MIN_VALUE; // label начинается с 0.
 *                return SimpleSuspendFunctionKt.myFunction((Continuation)this);
 *             }
 *          };
 *       }
 *
 *       // Это всё ещё тело suspend-функции:
 *       Object $result = ((<undefinedtype>)$continuation).result;
 *       Object var4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
 *       String var1;
 *       switch (((<undefinedtype>)$continuation).label) {
 *          case 0:
 *             ResultKt.throwOnFailure($result);
 *             var1 = "Before";
 *             System.out.println(var1);
 *             ((<undefinedtype>)$continuation).label = 1; // Меняется поля объекта Continuation! Увеличение лейбла.
 *             if (DelayKt.delay(1000L, (Continuation)$continuation) == var4) { // Важный момент - проверка: если delay вернул COROUTINE_SUSPENDED, то текущая suspend-функция возвращает COROUTINE_SUSPENDED! Аналогичные проверки есть у всех suspend-функций!
 *                return var4; // Возвращается маркер COROUTINE_SUSPENDED!
 *             }
 *             break; // Но даже если delay вернёт что-то другое, switch выйдет из кейса.
 *          case 1:
 *             ResultKt.throwOnFailure($result);
 *             break;
 *          default:
 *             throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
 *       }
 *
 *       var1 = "After";
 *       System.out.println(var1);
 *       return Unit.INSTANCE;
 *    }
 * }
 * ```
 */
suspend fun myFunction() {
    println("Before")
    delay(1000)
    println("After")
}