package io.github.priestoffern.vs_ship_assembler.physicify

import io.github.priestoffern.vs_ship_assembler.util.*
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import org.joml.Math
import org.joml.*
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD

fun physicifyBlocks(centerBlock: BlockPos, blocks: DenseBlockPosSet, level: ServerLevel, scale: Double): ServerShip {
    if (blocks.isEmpty()) throw IllegalArgumentException()

    // Find the bound of the ship to be physicified
    var structureCornerMin: BlockPos = BlockPos(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
    var structureCornerMax: BlockPos = BlockPos(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
    blocks.forEachChunk { chunkX, chunkY, chunkZ, chunk ->
        chunk.forEach { x, y, z ->
            structureCornerMin = BlockPos(
                Math.min(structureCornerMax.x, x),
                Math.min(structureCornerMax.y, y),
                Math.min(structureCornerMax.z, z)
            )
            structureCornerMax = BlockPos(
                Math.max(structureCornerMax.x, x),
                Math.max(structureCornerMax.y, y),
                Math.max(structureCornerMax.z, z)
            )
        }
    }

    // Create new contraption at center of bounds
    val shipWorldPos: Vector3d = structureCornerMin.toJOMLD().add(structureCornerMax.toJOMLD()).div(2.0)
    val shipPosition = ShipPosition(Quaterniond(0.0, 0.0, 0.0, 1.0), shipWorldPos, null)
    val shipBlockPos: BlockPos = createShipAt(level, shipPosition, scale)
    val ship: ServerShip = level.getShipManagingPos(shipBlockPos)!!


    var centerBlockReplaced = false
    blocks.forEachChunk { chunkX, chunkY, chunkZ, chunk ->
        chunk.forEach { x, y, z ->
            val relative: BlockPos = BlockPos(x, y, z).subtract(shipBlockPos.toJOML().toBlockPos())
            val shipPos: BlockPos = shipBlockPos.offset(relative)

            // Copy blocks and check if the center block got replaced (is default a stone block)
            copyBlock(level, BlockPos(x, y, z), shipPos)
            if (relative == BlockPos.ZERO) centerBlockReplaced = true

            // Remove block after replacing it
            removeBlock(level, BlockPos(x, y, z))

            // Trigger update
            triggerUpdate(level, BlockPos(x, y, z))
            triggerUpdate(level, shipPos)
        }
    }

    if (!centerBlockReplaced) {
        level.setBlock(shipBlockPos, Blocks.AIR.defaultBlockState(), 3)
    }

    teleportContraption(level, ship, shipPosition)

    return ship
}


private fun createShipAt(level: ServerLevel, shipPosition: ShipPosition, scale: Double): BlockPos {

    // Get parent ship (if existing)
    val parentShip: Ship? =
        level.getShipManagingPos(shipPosition.getPosition())

    // Apply parent ship translation if available
    if (parentShip != null) {
        shipPosition.toWorldPosition(parentShip.transform)
    }

    // Create new contraption
    val dimensionId: String = level.dimensionId
    val newShip: Ship = level.server.shipObjectWorld
        .createNewShipAtBlock(shipPosition.position.floorToVector3i(), false, scale, dimensionId)

    // Stone for safety reasons
    val centerBlockPos: BlockPos = toContraptionBlockPos(
        newShip.transform,
        BlockPos(shipPosition.position.floorToVector3i().toMinecraft())
    )
    level.setBlock(centerBlockPos, Blocks.STONE.defaultBlockState(), 3)

    // Teleport ship to final destination
    level.server.shipObjectWorld
        .teleportShip(newShip as ServerShip, shipPosition.toTeleport())
    return centerBlockPos
}