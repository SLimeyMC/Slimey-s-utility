package io.github.priestoffern.vs_ship_assembler.util



import io.github.priestoffern.vs_ship_assembler.VsShipAssemblerTags
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl
import org.valkyrienskies.mod.common.BlockStateInfo.onSetBlock
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.shipObjectWorld

object PhysicUtility {







    fun triggerBlockChange(level: Level?, pos: BlockPos?, prevState: BlockState?, newState: BlockState?) {
        onSetBlock(level!!, pos!!, prevState!!, newState!!)
    }

    fun isSolidContraptionBlock(state: BlockState?): Boolean {
        if (state != null) {
            return !state.tags.anyMatch { it== VsShipAssemblerTags.FORBIDDEN_ASSEMBLE }
        }
        return true
    }


    fun assembleToContraption(level: Level, blocks: List<BlockPos>, removeOriginal: Boolean, scale: Double): Boolean {
        assert(level is ServerLevel) { "Can't manage contraptions on client side!" }
        val sLevel: ServerLevel = level as ServerLevel
        if (blocks.isEmpty()) {
            return false
        }

        var structureCornerMin: BlockPos = blocks[0]
        var structureCornerMax: BlockPos = blocks[0]
        var hasSolids = false

        // Calculate bounds of the area containing all blocks adn check for solids and invalid blocks
        for (itPos in blocks) {
            if (isSolidContraptionBlock(level.getBlockState(itPos))) {
                structureCornerMin = GeneralUtility.getMinCorner(structureCornerMin, itPos)
                structureCornerMax = GeneralUtility.getMaxCorner(structureCornerMax, itPos)
                hasSolids = true
            }
        }
        if (!hasSolids) return false

        // Create new contraption at center of bounds
        val contraptionWorldPos: Vector3i = GeneralUtility.getMiddle(structureCornerMin, structureCornerMax)
        //val contraptionPosition = ContraptionPosition(Quaterniond(Vec3d(0.0, 1.0, 1.0), 0.0), contraptionWorldPos, null)


        val newShip: Ship = (level as ServerLevel).server.shipObjectWorld
            .createNewShipAtBlock(contraptionWorldPos, false, scale, level.dimensionId)

        // Stone for safety reasons

        val contraptionShipPos = newShip.worldToShip.transformPosition(Vector3d(contraptionWorldPos.x.toDouble(),contraptionWorldPos.y.toDouble(),contraptionWorldPos.z.toDouble()))
        val contraptionBlockPos = BlockPos(contraptionShipPos.x.toInt(),contraptionShipPos.y.toInt(),contraptionShipPos.z.toInt())


        // Copy blocks and check if the center block got replaced (is default a stone block)
        var centerBlockReplaced = false
        for (itPos in blocks) {
            if (isSolidContraptionBlock(level.getBlockState(itPos))) {
                val relative: BlockPos = itPos.subtract( BlockPos(contraptionWorldPos.x,contraptionWorldPos.y,contraptionWorldPos.z))
                val shipPos: BlockPos = contraptionBlockPos.offset(relative)
                GeneralUtility.copyBlock(level, itPos, shipPos)
                if (relative == BlockPos.ZERO) centerBlockReplaced = true
            }
        }

        // If center block got not replaced, remove the stone block
        if (!centerBlockReplaced) {
            level.setBlock(contraptionBlockPos, Blocks.AIR.defaultBlockState(), 3)
        }

        // Remove original blocks
        if (removeOriginal) {
            for (itPos in blocks) {
                if (isSolidContraptionBlock(level.getBlockState(itPos))) {
                    GeneralUtility.removeBlock(level, itPos)
                }
            }
        }

        // Trigger updates on both contraptions
        for (itPos in blocks) {
            val relative: BlockPos = itPos.subtract(BlockPos(contraptionWorldPos.x,contraptionWorldPos.y,contraptionWorldPos.z))
            val shipPos: BlockPos = contraptionBlockPos.offset(relative)
            GeneralUtility.updateBlock(level,itPos,shipPos,level.getBlockState(shipPos))
        }


        (level as ServerLevel).server.shipObjectWorld
            .teleportShip(newShip as ServerShip, ShipTeleportDataImpl(Vector3d(contraptionWorldPos.x.toDouble(),contraptionWorldPos.y.toDouble(),contraptionWorldPos.z.toDouble())))

        return true
    }


}