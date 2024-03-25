package kotlin_lang.kotlin_in_action._9_generics.sandbox

private interface Loot {
    fun fetchWeight(): Float
}

private class Weapon(
    private val name: String,
    val weight: Float,
    private var sharpness: Int
) : Loot {
    override fun fetchWeight(): Float = weight
    fun sharpen() { sharpness.inc() }
}

private class Box<out T : Loot>(initialLoot: List<T>) {
    private val loot = initialLoot.toMutableList()
    val size: Int
        get() = loot.size
    fun getLootByIndex(index: Int) = loot[index]
}

fun main() {
    val weaponBox = Box<Weapon>(
        initialLoot = listOf(
            Weapon("knife", 1.7f, 80),
            Weapon("sword", 5.2f, 95)
        )
    )

    /**
     * Функция принимает тип Box<Loot>, но я передаю Box<Weapon>.
     * Чтобы это было возможно, необходимо добавить out - ковариантность.
     */
    fun calculateWeightOfBox(loot: Box<Loot>) {
        var fullWeight: Float = 0f
        for (w in 0 until loot.size ) {
            fullWeight += loot.getLootByIndex(w).fetchWeight()
        }
        println("fullWeight: $fullWeight")
    }

    calculateWeightOfBox(weaponBox)
}