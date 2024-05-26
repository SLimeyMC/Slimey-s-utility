package io.github.slimeymc.slimeys_utility

import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import dev.architectury.registry.registries.DeferredRegister
import io.github.slimeymc.slimeys_utility.SlimeysUtilityMod.ID
import io.github.slimeymc.slimeys_utility.entity.ShipSelectionEntity
import io.github.slimeymc.slimeys_utility.entity.renderer.ShipSelectionEntityRenderer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.core.Registry
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

object SlimeysUtilityEntities {
    object SlimeysUtilityEntityType {
        val SHIP_SELECTION = EntityType.Builder.of(::ShipSelectionEntity, MobCategory.MISC).noSummon().build("ship_selection_entity")
    }

    val ENTITY_TYPES = DeferredRegister.create(ID, Registry.ENTITY_TYPE_REGISTRY)!!


    val SHIP_SELECTION_ENTITY = ENTITY_TYPES.register("ship_selection_entity")
    { SlimeysUtilityEntityType.SHIP_SELECTION }

    fun registerServer() {
        ENTITY_TYPES.register()
    }

    @Environment(EnvType.CLIENT)
    fun registerClient() {
        EntityRendererRegistry.register(SHIP_SELECTION_ENTITY) { ShipSelectionEntityRenderer(it) }
    }
}

