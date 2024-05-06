package io.github.slimeymc.slimeys_utility

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object SlimeysUtilityTags {

    @JvmField
    var FORBIDDEN_ASSEMBLE: TagKey<Block> = TagKey.create(
        Registry.BLOCK_REGISTRY,
        ResourceLocation(_root_ide_package_.io.github.slimeymc.slimeys_utility.SlimeysUtilityMod.MOD_ID, "forbidden_assemble")
    )
}