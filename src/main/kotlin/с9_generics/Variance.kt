package с9_generics

import kotlin.random.Random

/**
 * ## 9.3. Вариативность: обобщенные типы и подтипы.
 * Вариативность (variance) описывает, как связаны между собой типы
 * с одним базовым типом и разными типовыми аргументами, например:
 * List<String> и List<Any> - тут List - базовый тип, а String и Any - разные типовые аргументы.
 *
 * Зачем нужна вариантность: передача аргумента в функцию.
 * Допустим, есть функция, принимающая List<Any>. Безопасно ли передавать её List<String> ?
 */
fun printContents(list: List<Any>) {
    println(list.joinToString())
}

/**
 * Но если начать модифицировать список...
 */
fun addAnswer(list: MutableList<Any>) {
    list.add(42)
}

private fun e1() {
    val letters = listOf("a", "b")
    printContents(letters) // a, b

    val mutableLetters = mutableListOf("a", "b")
//    addAnswer(mutableLetters) <- Type mismatch.

    /**
     * Итак, безопасно передавать, если список неизменяемы. Если список изменяемый, передавать небезопасно.
     */
}

/**
 * Классы, типы и подтипы.
 * Тип переменной определяет её возможные значения.
 * Класс и тип не совсем одно и то же. Например, String и String? - то разные типы для одного класса.
 * С обобщёнными типами ещё сложнее. List - это не тип (это класс), типы это: List<Int>, List<String?> и т.д.
 * Понятие типа.
 * Тип B - это подтип А, если значение типа В можно использовать везде, где ожидается значение типа А. Тип одновременно
 * является подтипом самого себя. Термин супертип противоположен термину подтип.
 * ! Каждый раз при присвоении значения переменной или передаче аргумента в вызов функции, компилятор проверяет наличие отношения тип-подтип.
 *
 * В большинстве случаев подтип означает то же самое, что и подкласс. Но nullable типы показывают случаи, когда подтип - не то же самое, что подкласс.
 * Int? <- Int; Int X<- Int? - Int является подтипом Int?, но не наоборот.
 *
 * Разница между подклассами и подтипами становится особенно важно в обобщённых типах: является ли List<String> подтипом List<Any> ?
 *
 * Обобщённый класс называется инвариантным по типовому параметру, если для любых типов А и В Class<A> не является подтипом или супертипом Class<B>.
 * В Java все классы инвариантны (MutableList<String> не является подтипом MutableList<Any>).
 *
 * Если А - это подтип В, а Class<A> - это подтип Class<B>, такие класс и интерфейсы называются ковариантными (List<String> - это подтип List<Any>).
 * Ковариантность - это когда направление отношения тип-подтип сохраняется.
 * Ковариантный класс - это обобщённый класс, для которого верно следующее: Producer<A> - это подтип Producer<B>, если А - это подтип В.
 * Это называется сохранением направления отношения тип-подтип.
 * В Kotlin, чтобы объявить класс ковариантным по некоторому типовому параметру, нужно добавить ключевое слово "out" перед именем типового параметра.
 *
 * Этот класс объявлен ковариантным по типовому параметру Т. out разрешает передавать значения этого класса, когда типовой аргумент
 * неточно соответствует типу параметра.
 */
interface Producer<out T> {
    fun produce(): T
}

private fun e2() {
    open class Animal {
        fun feed() {}
    }

    class Herd<T : Animal> { //<- типовой параметр не объявлен ковариантным
        private val animals = mutableListOf<T>()
        val size: Int get() = 0
        operator fun get(i: Int): T = animals[i]
    }

    fun feedAll(herd: Herd<Animal>) {
        for (i in 0 until herd.size) {
            herd[i].feed()
        }
    }

    class Cat : Animal() {
        fun cleanLitter() {}
    }

    fun takeCareOfCats(cats: Herd<Cat>) {
        for (i in 0 until cats.size) {
            cats[i].cleanLitter()
        }
        //feedAll(cats) // ERROR: Type mismatch. Required: Herd<Animal> Found: Herd<Cat>
        // feedAll принимает Herd<Animal>, передаётся Herd<Cat>.
        // Так как Herd не объявлен ковариантным Herd<Cat> не считается подтипом Herd<Animal>, несмотря на то что Cat - это подтип Animal.
        // Стадо кошек не считается подтипом стада животных (
    }
}

private fun e3() {
    open class Animal {
        fun feed() {}
    }

    class Herd<out T : Animal> { //<- типовой параметр объявлен ковариантным
        private val animals = mutableListOf<T>()
        val size: Int get() = 0
        operator fun get(i: Int): T = animals[i]
    }

    fun feedAll(herd: Herd<Animal>) {
        for (i in 0 until herd.size) {
            herd[i].feed()
        }
    }

    class Cat : Animal() {
        fun cleanLitter() {}
    }

    fun takeCareOfCats(cats: Herd<Cat>) {
        for (i in 0 until cats.size) {
            cats[i].cleanLitter()
        }
        feedAll(cats) // <- feedAll принимает Herd<Animal>, а также Herd<Cat>.
        // Стадо кошек считается подтипом стада животных
    }
}

/**
 * Не каждый класс можно объявить ковариантным: это может быть небезопасно.
 * Чтобы гарантировать безопасность типов, он может использоваться только в исходных (out) позициях:
 * то есть класс может производить значения типа Т, но не потреблять их.
 *
 * Использование типового параметра в объявлениях членов класса можно разделить на входящие (in) и исходящие (out) позиции.
 *
 * Если Т используется как тип возвращаемого значения функции, то он находится в исходящей позиции. Функция производит значение типа Т.
 *
 *      interface Producer<out T> {
 *           fun produce(): T <- исходящая позиция
 *      }
 *
 * Если Т используется как тип параметра функции, он находится во входящей позиции. Функция потребляет значения типа Т.
 *
 *      interface Consumer<in T> {
 *          fun consume(t: T) <- входящая позиция.
 *      }
 *
 * Ключевое слово out требует, чтобы все методы, использующие Т, указывали его только в исходящей позиции (in - во входящей).
 *
 * В интерфейсе List<out T> - все T находятся в исходящей позиции. Поэтому он ковариантный.
 * В интерфейсе MutableList<T> - T находятся в обеих позициях.
 *
 * Параметры конструктора не находятся ни во входящей, ни в исходящей позиции.
 *
 *          class Herd<out T : Animal>(vararg animals: T)  { }
 *
 * Вариантность защищает от ошибок при вызове методов. Конструктор - это не метод, который можно некорректно вызвать у экземпляра, поэтому он не представляет опасности.
 * Однако при объявлении свойств в конструкторе через val или var к ним добавляются методы доступа. Поэтому параметр типа оказывается в исходящей позиции для val, и в обеих позициях для var.
 *
 *           // тут уже нельзя использовать ковариантность*
 *           class Herd<T : Animal>(var leadAnimal: T, vararg animals: T)  { }
 *
 * Правила позиции относятся только к видимому извне API, и не относятся к внутренней реализации класса.
 *
 *          class Herd<out T : Animal>(private var leadAnimal: T, vararg animals: T)  { }
 *
 * Контравариантность - понятие обратное ковариантности: для контравариантного класса отношение тип-подтип действует в обратном направлении
 * относительно отношения между классами, использованными как типовые аргументы.
 *
 *      interface Comparator<in T> {
 *           fun compare(e1: T, e2: T): Int {...} //<- T используется во входящей позиции
 *      }
 *
 * Реализация этого интерфейса для типа A может сравнивать значения любых его подтипов.
 */
private fun e4() {
    val anyComparator = Comparator<Any> {
            e1, e2 -> e1.hashCode() - e2.hashCode()
    }
    val strings = listOf("c", "f", "a", "b")
    println(strings.sortedWith(anyComparator)) // [a, b, c, f]

    /**
     * Функция sortedWith ожидает получить Comparator<String>, но принимает Comparator<Any>. Это означает, что Comparator<Any> - подтип Comparator<String>,
     * тогда как Any - это супертип String. Отношение вида тип-подтип между обобщёнными типами противоположно направлено отношению между типами их типовых аргументов!
     *
     * Класс, контравариантный по типовому параметру, — это обобщённый класс, для которого выполняется следующее:
     * Consumer<A> - это подтип Consumer<B>, если В - это подтип А.
     */
}

/**
 * Класс может быть ковариантным по одному параметру типа и контравариантным - по другому.
 * Классический пример, интерфейс Function, использующийся для создания лямбд (функциональных типов?)
 *
 *      interface Function1<in P, out R> {
 *          operator fun invoke(p: P): R
 *      }
 *
 *      (P) -> R - форма записи, эквивалентная Function1<R, P>
 *
 * Понятие подтипа для функциональных типов обратно обратно первому типовому аргументу (in) и совпадает со вторым (out).
 */
private fun e5() {
    fun enumerateCats(f: (Cat) -> Number) {}
    fun Animal.getIndex(): Int = 0
    enumerateCats(Animal::getIndex) //<- допустимый код, тк Animal - супертип Cat, а параметр лямбд ковариантен.
}

/**
 * Установка вариантности с помощью "in", "out" называется определением вариантности в месте объявления (это более лаконично).
 * В Java вариантность обрабатывается иначе. Всякий раз, когда в Java используется тип с параметром типа, есть возможность
 * указать, что в параметре типа можно передавать типы и супертипы. Это называется определением вариантности в месте использования.
 *
 *     public static void iterateAnimals(Collection<Animal> animals) {}
 *
 *     public static void iterateAnimalsWildcard(Collection<? extends Animal> animals) {}
 *
 *     public static void main(String[] args) {
 *         List<Cat> cats = new ArrayList<>();
 *         cats.add(new Cat());
 *         cats.add(new Cat());
 *
 *         // iterateAnimals(cats); ERROR
 *         iterateAnimalsWildcard(cats);
 *     }
 *
 * Kotlin тоже поддерживает возможность определение вариантности в месте использования.
 * MutableList - инвариантен. Но иногда может использоваться только для чтения или только для записи.
 *
 * @param source используется только для чтения
 * @param destination используется только для записи
 */
fun <T> copyData(source: MutableList<T>, destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    }
}

/**
 * Для подобной операции типы коллекции могут не совпадать (можно копировать из List<String> в List<Any>).
 * Тип элементов в исходной коллекции должен быть подтипом типа элементов в коллекции назначения.
 */
fun <T : R, R> copyData1(source: MutableList<T>, destination: MutableList<R>) {
    for (item in source) {
        destination.add(item)
    }
}

/**
 * То же можно записать более элегантно - указать модификатор вариантности в место использования типового параметра (не в месте объявления).
 * Такое определение называется проекцией типа (type projection): оно говорит, что source - это проекция типа MutableList с ограниченными возможностями.
 */
fun <T> copyData2(source: MutableList<out T>, destination: MutableList<in T>) {
    for (item in source) {
        destination.add(item)
    }
}

private fun e6() {
    val outList: MutableList<out Number> = mutableListOf(1, 2, 3)
    println(outList[1]) // 2
    // outList.add(4) <- ERROR: Типовой параметр в out позиции используется в in позиции
}

/**
 * Определение вариантности в месте использования в Kotlin прямо соответствует wildcards в Java.
 * MutableList<out T> -> MutableList<? extends T>
 * MutableList<in T> -> MutableList<? super T>
 *
 * ## Проекция со звёздочкой: использование * вместо аргумента типа.
 *
 */
private fun e7() {
    val list: MutableList<Any?> = mutableListOf('a', 1, "qwe")
    val chars = mutableListOf('a', 'b', 'c')
    val unknownElements: MutableList<*> = if (Random.nextBoolean()) list else chars
//    unknownElements.add(42) ERROR
    unknownElements.first() //<- извлечение (out) не представляет опасности

    /**
     * Компилятор интерпретирует MutableList<*> как исходящую проекцию типа (как MutableList<out Any?>): если о типе элементов
     * ничего не известно, безопасно только извлекать элементы, а добавлять - опасно.
     *
     * Если провести аналогию с wildcard Java:
     * MyType<*> -> MyType<?>
     *
     * Синтаксис проекций со звёздочкой используется, когда информация о типовом аргументе не имеет значения, то есть
     * когда код не использует методов, ссылающихся на типовой аргумент, или только читает данные без учёта их типа.
     */
    fun printFirst(list: List<*>) {
        if (list.isNotEmpty()) {
            println(list.first())
        }
    }
    /**
     * Можно реализовать то же с введением параметра обобщённого типа, но синтаксис со звёздочкой лаконичнее.
     */
    fun <T> printFirst1(list: List<T>) {
        if (list.isNotEmpty()) {
            println(list.first())
        }
    }
}


fun main() {
    e1()
    e4()
    e6()
}


//util area:
open class Animal {
    fun feed() {}
}
class Cat : Animal() {
    fun cleanLitter() {}
}