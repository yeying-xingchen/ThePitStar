package real.nanoneko

import cn.charlotte.pit.ThePit
import net.mizukilab.pit.enchantment.AbstractEnchantment
@Deprecated("No use")
object EnchantedConstructor {
    private val enchantments: MutableList<Class<AbstractEnchantment>> = mutableListOf()

    fun getEnchantments(): List<Class<AbstractEnchantment>> {
        return enchantments
    }

    fun addEnchantment(enchantment: Class<*>) {

        println("This class is deprecated, please use EnchantmentFactor.init(...), this feature will be removed in future!!!")
        if (AbstractEnchantment::class.java.isAssignableFrom(enchantment)) {
            enchantments.add(enchantment as Class<AbstractEnchantment>);
        } else {
            throw IllegalArgumentException("Only classes implementing AbstractEnchantment can be added as enchantments.")
        }
    }
    fun removeEnchantment(enchantment: Class<*>) {
        enchantments.remove(enchantment)
    }
}