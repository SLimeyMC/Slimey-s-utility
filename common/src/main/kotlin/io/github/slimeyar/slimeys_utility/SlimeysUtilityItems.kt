package io.github.slimeyar.slimeys_utility

import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import io.github.slimeyar.slimeys_utility.items.ShipAssemblerItem
import net.minecraft.world.item.CreativeModeTab.TAB_MISC

object SlimeysUtilityItems {
    val ITEMS = DeferredRegister.create(SlimeysUtilityMod.MOD_ID, Registry.ITEM_REGISTRY)

    var SHIP_ASSEMBLER: RegistrySupplier<Item> = ITEMS.register("ship_assembler")
    { ShipAssemblerItem(Item.Properties().tab(TAB_MISC).stacksTo(1)) }
    fun register() {
        ITEMS.register()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}