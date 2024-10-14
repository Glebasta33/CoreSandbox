package coroutines.coroutines_deepdive._1_understanding_coroutines

/**
 * # Sequence builder.
 * Последовательности в котлин - это концепция, схожая с коллекциями, но они ленивые.
 * (ниже дан пример работы механизма приостановки (suspension)).
 */
object SequenceBuilder {
    init {
        println("SequenceBuilder:")
        // билдер последовательности
        val seq = sequence {
            yield(1) // yield - выдать
            yield(2)
            yield(3)
        }

        for (num in seq) { // элементы последовательности выдаются по требованию
            print(num)
        } // 123
    }

    init {
        val seq = sequence {
            println("Generating first")
            yield(1) // выдача первого числа
            println("Generating second")
            yield(2)
            println("Generating third")
            yield(3)
            println("Done")
        }

        for (num in seq) { // получение числа из последовательности
            println("The next number is $num")
        //!! Далее выполнение переходит обратно в sequence в место, где ранее была остановка.
        // Это было бы невозможно без механизма приостановки (suspension).
        }
        //Generating first
        //The next number is 1
        //Generating second
        //The next number is 2...
    }

    /**
     * Чтобы рассмотреть процесс более ясно, запросим значения из последовательности вручную.
     * Код приостанавливается в определённом месте в определённом месте внутри одного скоупа,
     * затем выполнение преходит в другой скоуп, а после - возвращается и возобновляется в месте предыдущей остановки!
     */
    init {
        val seq = sequence {
            println("Generating first")
            yield(1)
            println("Generating second")
            yield(2)
            println("Generating third")
            yield(3)
            println("Done")
        }

        val iterator = seq.iterator()
        println("Starting")
        val first = iterator.next()
        println("First: $first")
        val second = iterator.next()
        println("Second: $second")
        //Starting
        //Generating first
        //First: 1
        //Generating second
        //Second: 2
    }
}