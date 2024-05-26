package io.github.slimeymc.slimeys_utility.items

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import org.joml.Math
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet


fun countBlockOccurrences(level: Level, blocks: DenseBlockPosSet, obtainable: Boolean): Map<Block, Int> {
    val blockMap = mutableMapOf<Block, Int>()
    blocks.forEachChunk { chunkX, chunkY, chunkZ, chunk ->
        chunk.forEach { x, y, z ->
            val currentBlock = level.getBlockState(BlockPos(x, y, z)).block
            // if (currentBlock.) TODO (Detect if currentBlock is unobtainable with survival or doesnt have item counterpart)
            blockMap[currentBlock] = (blockMap[currentBlock] ?: 0) + 1
        }
    }
    return blockMap.toMap() // Create an immutable map
}

fun RemoveOrCountBlockFromInventory(player: Player, blockMap: Map<Block, Int>): Boolean {
    blockMap.forEach { (block, amount) ->
        if (player.inventory.countItem(block.asItem()) < amount) return false
        //player.containerMenu.carried
    }

    blockMap.forEach { (block, amount) ->
        player.inventory.removeItem(ItemStack(block.asItem(), amount))
    }
    return true
}