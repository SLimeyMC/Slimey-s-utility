package io.github.slimeymc.slimeys_utility

import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import io.github.slimeymc.slimeys_utility.items.ShipAssemblerItem
import io.github.slimeymc.slimeys_utility.items.ShipScalerItem
import io.github.slimeymc.slimeys_utility.items.ShipTaggerItem
import net.minecraft.world.item.CreativeModeTab.TAB_MISC

object SlimeysUtilityItems {
    val ITEMS = DeferredRegister.create(_root_ide_package_.io.github.slimeymc.slimeys_utility.SlimeysUtilityMod.MOD_ID, Registry.ITEM_REGISTRY)

    var SHIP_ASSEMBLER: RegistrySupplier<Item> = io.github.slimeymc.slimeys_utility.SlimeysUtilityItems.ITEMS.register("ship_assembler")
    { ShipAssemblerItem(Item.Properties().tab(TAB_MISC).stacksTo(1)) }
    var SHIP_TAGGER: RegistrySupplier<Item> = io.github.slimeymc.slimeys_utility.SlimeysUtilityItems.ITEMS.register("ship_tagger")
    { ShipTaggerItem(Item.Properties().tab(TAB_MISC).stacksTo(1)) }
    var SHIP_SCALER: RegistrySupplier<Item> = io.github.slimeymc.slimeys_utility.SlimeysUtilityItems.ITEMS.register("ship_scaler")
    { ShipScalerItem(Item.Properties().tab(TAB_MISC).stacksTo(1)) }
    fun register() {
        io.github.slimeymc.slimeys_utility.SlimeysUtilityItems.ITEMS.register()
    }

    private infix fun Item.byName(name: String) = io.github.slimeymc.slimeys_utility.SlimeysUtilityItems.ITEMS.register(name) { this }
}