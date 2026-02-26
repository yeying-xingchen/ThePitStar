package real.nanoneko

/**
 * @author Araykal
 * @since 2025/1/31
 */
@Deprecated("No use")
object PerkConstructor {
    private val perks: MutableList<Class<*>> = mutableListOf()

    fun getPerks(): List<Class<*>> {
        return perks
    }

    fun addPerk(enchantment: Class<*>) {
        println("This class is deprecated, please use EnchantmentFactor.init(...), this feature will be removed in future!!!")
        perks.add(enchantment)
    }

    fun removePerk(enchantment: Class<*>) {
        perks.remove(enchantment)
    }
}