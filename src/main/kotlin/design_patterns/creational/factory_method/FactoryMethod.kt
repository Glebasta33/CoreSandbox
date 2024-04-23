package design_patterns.creational.factory_method

// https://www.geeksforgeeks.org/factory-method-for-designing-pattern/?ref=lbp

/**
 * ## Фабричный метод (Factory Method Design Pattern).
 * ## Компоненты:
 */

// 1. Creator (Factory) - обстрактный класс или интерфейс, определяющий фабричный метод.
interface VehicleFactory {
    fun createVehicle(): Vehicle // <- Фабричный метод, объявляющий тип объектов, которые будут создаваться
}

// 2. Concrete Creator (Factory) - конкретная фабрика, возвращающая конкретные объекты.
// Создают конкретные типы объектов
class TwoWheelerFactory : VehicleFactory {
    override fun createVehicle(): Vehicle {
        return TwoWheeler()
    }
}

class FourWheelerFactory : VehicleFactory {
    override fun createVehicle(): Vehicle {
        return FourWheeler()
    }
}

// 3. Product - обстрактный класс или интерфейс для объектов, которые создаёт фабричный метод.
// Product задаёт общий интерфейс для всех объектов, которые создаются фабричным методом.
abstract class Vehicle {
    abstract fun printVehicle() // общий метод для создаваемых объектов
}

// 4. Concrete Product - класс конкретного объекта, который создаётся фабричным методом.
class TwoWheeler : Vehicle() {
    override fun printVehicle() {
        println("I am two wheeler")
    }
}

class FourWheeler : Vehicle() {
    override fun printVehicle() {
        println("I am four wheeler")
    }
}

class Client(factory: VehicleFactory) {
    val vehicle: Vehicle = factory.createVehicle()
}

fun main() {
    val twoWheelerFactory: VehicleFactory = TwoWheelerFactory()
    val twoWheelerClient = Client(twoWheelerFactory)
    val twoWheeler: Vehicle = twoWheelerClient.vehicle
    twoWheeler.printVehicle()

    val fourWheelerFactory: VehicleFactory = FourWheelerFactory()
    val fourWheelerClient = Client(fourWheelerFactory)
    val fourWheeler: Vehicle = fourWheelerClient.vehicle
    fourWheeler.printVehicle()
}

/**
 * Преимущества Фабричного метода:
 * - Снижение связности кода. Разделяются объект с логикой создания (порождения) новых объектов (Creator (Factory))
 * и потребитель создаваемых объектов (Client) -> Изменение логики создания объектов не требуют изменения логики клиента.
 * - Расширяемость. Можно легко добавить новый тип продукта,  не внося изменения в код клиента.
 * - Переиспользуемость. Можно переиспользовать фабрику для создания объектов в разных местах приложения, а логика
 * создания будет храниться в одном месте/
 *
 * Недостатки:
 * - Увеличение сложности. Дополнительные интерфейсы и классы.
 * ...
 */