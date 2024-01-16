package c3_functions

/**
 * # Kotlin в действии.
 * ## 3.6 Чистим код. Локальные функции.
 */

/**
 * При рефакторинге дублирующийся код полезно выносить в отдельный метод, но это делает код трудным для понимания,
 * потому что в классе - множество мелких методов без чёткой структуры.
 *
 * Kotlin - предлагает локальные (вложенные) функции как способ сделать код чище, структурировать его.
 */

data class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {
    fun validate(value: String, fieldName: String) { //локальная функция
        if (value.isEmpty()) {
            throw IllegalArgumentException("Can`t save user ${user.id}: " + //локальные функции имеют доступ к параметрам и переменным охватывающей функции.
                    "empty $fieldName")
        }
    }

    validate(user.name, "Name")
    validate(user.address, "Address")

    //Сохранение данных ...
    println(user)
}

fun main() {
    val user = User(1, "Ivan", "Moscow")
    saveUser(user)
}