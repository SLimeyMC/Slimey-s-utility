package io.github.slimeymc.slimeys_utility.integrations

import dev.latvian.mods.kubejs.KubeJSPlugin
import dev.latvian.mods.kubejs.script.BindingsEvent
import io.github.slimeymc.slimeys_utility.items.*
import io.github.slimeymc.slimeys_utility.ship.ShipData
import io.github.slimeymc.slimeys_utility.util.RelocateLevel

class SlimeysUtilityKubeJSPlugin: KubeJSPlugin() {
    override fun addBindings(event: BindingsEvent) {
        // Utility stuff
        event.add("ShipData", ShipData::class.java)
        event.add("RelocateLevel", RelocateLevel::class.java)

        // Item
        event.add("ShipSelectingItem", ShipSelectionItem::class.java)
        event.add("ShipAssemblerItem", ShipAssemblerItem::class.java)
        event.add("ShipScalerItem", ShipScalerItem::class.java)
        event.add("ShipTaggerItem", ShipTaggerItem::class.java)
    }
}