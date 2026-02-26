package real.nanoneko

/**
 * @author Araykal
 * @since 2025/1/31
 */
@Deprecated("No use")
object ItemConstructor {
    private val items: MutableList<Class<*>> = mutableListOf()

    fun getItems(): List<Class<*>> {
        return items
    }

    fun addItems(enchantment: Class<*>) {
        println("This class is deprecated, please use PerkFactory.init(...), this feature will be removed in future!!!")
        items.add(enchantment)
    }

    fun removeItems(enchantment: Class<*>) {
        items.remove(enchantment)
    }
}