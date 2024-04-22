package design_patterns.creational.factory_method

// TODO: Реализовать Java пример фабричного метода на Kotlin https://www.geeksforgeeks.org/factory-method-for-designing-pattern/?ref=lbp

/**
 * ## Фабричный метод (Factory Method Design Pattern).
 * ## Компоненты:
 */

// 1. Creator - обстрактный класс или интерфейс, определяющий фабричный метод
abstract class Vehicle {
    abstract fun printVehicle()
}

// 2. Concrete Creator - конкретный класс, реализующий Creator.
class TwoWheeler : Vehicle() {
    override fun printVehicle() {
        println("I am two wheeler")
    }
}

// 3. Product - обстрактный класс или интерфейс для объектов, которые создаёт фабричный метод.
// Задаёт общий интерфейс для всех проектов, которые создаются фабричным методом.

fun main() {

}