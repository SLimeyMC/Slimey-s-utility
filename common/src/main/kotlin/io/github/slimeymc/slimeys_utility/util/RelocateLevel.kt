package io.github.slimeymc.slimeys_utility.util

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.ticks.ScheduledTick
import org.valkyrienskies.mod.common.BlockStateInfo

class RelocateLevel {
    val level: Level

    constructor(level: Level) {
        this.level = level
    }

    fun setBlock(pos: BlockPos, state: BlockState) {
        val chunk = level.getChunk(pos) as LevelChunk
        val section = chunk.getSection(chunk.getSectionIndex(pos.y))
        val oldState = level.getBlockState(pos)
        section.setBlockState(pos.x and 15, pos.y and 15, pos.z and 15, state)
        triggerBlockChange(pos, oldState, state)
    }

    fun removeBlock(pos: BlockPos) {
        level.removeBlockEntity(pos)
        setBlock(pos, Blocks.AIR.defaultBlockState())
    }

    fun copyBlock(from: BlockPos, to: BlockPos) {
        val state = level.getBlockState(from)
        val blockentity = level.getBlockEntity(from)
        setBlock(to, state)

        // Transfer pending schedule-ticks
        if (level.blockTicks.hasScheduledTick(from, state.block)) {
            level.blockTicks.schedule(ScheduledTick<Block?>(state.block, to, 0, 0))
        }

        // Transfer block-entity data
        if (state.hasBlockEntity() && blockentity != null) {
            val data: CompoundTag = blockentity.saveWithId()
            level.setBlockEntity(blockentity)
            val newBlockentity = level.getBlockEntity(to)
            newBlockentity?.load(data)
        }
    }

    fun triggerUpdate(pos: BlockPos) {
        val chunk = level.getChunkAt(pos)
        level.sendBlockUpdated (pos, level.getBlockState(pos), level.getBlockState(pos), 3) //markAndNotifyBlock(pos, chunk, level.getBlockState(pos), level.getBlockState(pos), 3, 512)
        level.updateNeighborsAt(pos,level.getBlockState(pos).block)
    }

    fun triggerBlockChange(pos: BlockPos, prevState: BlockState, newState: BlockState) {
        BlockStateInfo.onSetBlock(level, pos, prevState, newState)
    }
}