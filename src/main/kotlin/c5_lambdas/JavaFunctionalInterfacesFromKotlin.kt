package c5_lambdas

import kotlin_in_action.lambdas.Button
import kotlin_in_action.lambdas.ComputationUtils

/**
 * # 5.4 Использование функциональных интерфейсов из Java в Kotlin
 *
 * Большая часть API написана на Java. Лямбда-выражения полностью совместимы с Java.
 *
 * Пример. В Java определён функциональный интерфейс или SAM (Single Abstract Method):
 * @FunctionalInterface
 * public interface OnClickListener {
 *     public void onClick();
 * }
 *
 * Kotlin способен использовать лямбда-выражения для вызова подобных методов из Java.
 */
fun main() {
    val button = Button() // Java-класс
//   Анонимный класс в Java:
//    button.setOnClickListener(new OnClickListener() {
//        @Override
//        public void onClick() {
//            /* действия по щелчку */
//        }
//    });
    button.setOnClickListener { /* действия по щелчку */ } // <- лямбда вместо анонимного класса в Kotlin!

    // Можно передать лямбда-выражение в любой метод Java, принимающий функциональный интерфейс.
    // Под капотом создастся объект анонимного класса, реализующий функциональный интерфейс.
    // Есть метод Java: void postponeComputation(int delay, Runnable computation) {...}
    val utils = ComputationUtils()
    utils.postponeComputation(1000)  { println(42) }

    // Того же самого можно добиться, явно создав анонимный объект, реализующий интерфейс Runnable:
    utils.postponeComputation(1000, object : Runnable {
        override fun run() {
            println(42)
        }
    })
    // Но при таком явном объявлении анонимного объекта при каждом вызове создаётся новый экземпляр.
    // При использовании лямбд (если они не захватывают внешних переменных) создаётся один экземпляр для всех вызовов.
    // В лямбдах работает примерно такая логика:
    val runnable = object : Runnable {
        override fun run() {
            println(42)
        }
    }
    utils.postponeComputation(1000, runnable)

    /**
     * SAM-конструктор - это функция, сгенерированная компилятором, которая позволяет явно выполнить преобразование
     * лямбда-выражения в экземпляр функционального интерфейса.
     * SAM-конструктор принимает 1 аргумент - лямбда-выражение, которе будет использовано как тело метода в функциональном интерфейсе.
     */
    fun createAllDoneRunnable(): Runnable {
        return Runnable { println("All done!") }
    }
    createAllDoneRunnable().run() // All done!
}
