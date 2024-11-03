package coroutines.coroutines_deepdive._1_understanding_coroutines.underthehood.sandbox

import kotlinx.coroutines.delay

/**
 * printUser под капотом:
 * ```
 *    @Nullable
 *    public static final Object printUser(@NotNull String token, @NotNull Continuation $completion) {
 *       Object $continuation;
 *       label27: {
 *          if ($completion instanceof <undefinedtype>) {
 *             $continuation = (<undefinedtype>)$completion;
 *             if ((((<undefinedtype>)$continuation).label & Integer.MIN_VALUE) != 0) {
 *                ((<undefinedtype>)$continuation).label -= Integer.MIN_VALUE;
 *                break label27;
 *             }
 *          }
 *
 *          $continuation = new ContinuationImpl($completion) { // Создаётся объект Continuation функции printUser
 *             Object L$0;
 *             // $FF: synthetic field
 *             Object result;
 *             int label;
 *
 *             @Nullable
 *             public final Object invokeSuspend(@NotNull Object $result) {
 *                this.result = $result;
 *                this.label |= Integer.MIN_VALUE;
 *                return FunctionsResumedWithValueKt.printUser((String)null, (Continuation)this);
 *             }
 *          };
 *       }
 *
 *       Object var10000;
 *       String userId;
 *       String userName;
 *       label22: {
 *          Object $result = ((<undefinedtype>)$continuation).result;
 *          Object var7 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
 *          switch (((<undefinedtype>)$continuation).label) {
 *             case 0:
 *                ResultKt.throwOnFailure($result);
 *                userId = "Before";
 *                System.out.println(userId);
 *                ((<undefinedtype>)$continuation).L$0 = token;
 *                ((<undefinedtype>)$continuation).label = 1;
*                // объект Continuation функции printUser передаётся во внутренние suspend-функции, где будет обёрнут в их Continuation !
 *                var10000 = getUserId(token, (Continuation)$continuation);
 *                if (var10000 == var7) {
 *                   return var7;
 *                }
 *                break;
 *             case 1:
 *              // Из объекта Continuation достаётся стейт с данными!
 *                token = (String)((<undefinedtype>)$continuation).L$0;
 *                ResultKt.throwOnFailure($result);
 *                var10000 = $result;
 *                break;
 *             case 2:
 *              // Из объекта Continuation достаётся стейт с данными!
 *                userId = (String)((<undefinedtype>)$continuation).L$0;
 *                ResultKt.throwOnFailure($result);
 *                var10000 = $result;
 *                break label22;
 *             default:
 *                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
 *          }
 *
 *          userId = (String)var10000;
 *          userName = "Got userId: " + userId;
 *          System.out.println(userName);
 *          ((<undefinedtype>)$continuation).L$0 = userId;
 *          ((<undefinedtype>)$continuation).label = 2;
 *          var10000 = getUserName(userId, token, (Continuation)$continuation);
 *          if (var10000 == var7) {
 *             return var7;
 *          }
 *       }
 *
 *       userName = (String)var10000;
 *       String var4 = "User: " + userId + ", " + userName + ';';
 *       System.out.println(var4);
 *       var4 = "After";
 *       System.out.println(var4);
 *       return Unit.INSTANCE;
 *    }
 * ```
 *
 * А теперь краеугольный камень корутин:
 * ## Вложенные suspend-функции и call stack из объектов Continuation.
 * Когда функция a вызывает функцию b, необходимо где-то хранить состояние из a
 * (особенно в случае освобождения потока, ведь стэк вызова с локальными данными потока будет очищен !).
 * В корутинах Continuation служит как стек вызовов. Каждый объект Continuation хранит состояние о месте
 * приостановки (label), локальные переменные, а также ссылку на объект Continuation функции, которая вызвала текущую функцию.
 *
 * В результате чего образуется многослойная структура Continuations, которая выглядит примерно так:
 * ```
 * suspend fun a() { b() }
 * suspend fun b() { c() }
 * suspend fun c() { ... }
 *
 * CContinuation(
 *      i =  4,
 *      label = 1,
 *      completion = BContinuation(
 *          i = 4,
 *          label = 1,
 *          completion = AContinuation(...)
 *      )
 * )
 * ```
 *
 */
suspend fun printUser(token: String) {
    println("Before")
    val userId = getUserId(token)
    println("Got userId: $userId")
    val userName = getUserName(userId, token)
    println("User: $userId, $userName;")
    println("After")
}

suspend fun getUserId(token: String): String {
    delay(1000)
    return "uid-123243"
}

suspend fun getUserName(id: String, token: String): String {
    delay(2000)
    return "John Snow"
}