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
import org.valkyrienskies.mod.common.util.toMinecraft

fun physicifyBlocks(blocks: DenseBlockPosSet, level: ServerLevel, scale: Double): ServerShip {
    if (blocks.isEmpty()) throw IllegalArgumentException()

    // Find the bound of the ship to be physicified
    var structureCornerMin= BlockPos(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
    var structureCornerMax= BlockPos(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
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
    val shipData = ShipData(Quaterniond(0.0, 0.0, 0.0, 1.0), shipWorldPos, null)
    val shipBlockPos: BlockPos = createShipAt(level, shipData, scale)
    val ship: ServerShip = level.getShipManagingPos(shipBlockPos)!!

    // Make a class to help with moving out the block
    val relocateLevel= RelocateLevel(level)


    var centerBlockReplaced = false
    blocks.forEachChunk { chunkX, chunkY, chunkZ, chunk ->
        chunk.forEach { x, y, z ->
            val relative: BlockPos = BlockPos(x, y, z).subtract(shipBlockPos.toJOML().toBlockPos())
            val shipPos: BlockPos = shipBlockPos.offset(relative)

            // Copy blocks and check if the center block got replaced (is default a stone block)
            relocateLevel.copyBlock(BlockPos(x, y, z), shipPos)
            if (relative == BlockPos.ZERO) centerBlockReplaced = true

            // Remove block after replacing it
            relocateLevel.removeBlock(BlockPos(x, y, z))

            // Trigger update
            relocateLevel.triggerUpdate(BlockPos(x, y, z))
            relocateLevel.triggerUpdate(shipPos)
        }
    }

    if (!centerBlockReplaced) {
        level.setBlock(shipBlockPos, Blocks.AIR.defaultBlockState(), 3)
    }

    teleportContraption(level, ship, shipData)

    return ship
}

private fun teleportContraption(level: ServerLevel, ship: ServerShip, position: ShipData) {
    level.server.shipObjectWorld
        .teleportShip(ship, position.toTeleport())
}

private fun createShipAt(level: ServerLevel, shipData: ShipData, scale: Double): BlockPos {

    // Get parent ship (if existing)
    val parentShip: Ship? =
        level.getShipManagingPos(shipData.getPosition())

    // Apply parent ship translation if available
    if (parentShip != null) {
        shipData.toWorldPosition(parentShip.transform)
    }

    // Create new contraption
    val dimensionId: String = level.dimensionId
    val newShip: Ship = level.server.shipObjectWorld
        .createNewShipAtBlock(shipData.position.floorToVector3i(), false, scale, dimensionId)

    // Stone for safety reasons
    val t = Vector3d()
    val centerBlockPos= BlockPos(newShip.shipAABB!!.center(t).toMinecraft())
    level.setBlock(centerBlockPos, Blocks.STONE.defaultBlockState(), 3)

    // Teleport ship to final destination
    level.server.shipObjectWorld
        .teleportShip(newShip as ServerShip, shipData.toTeleport())
    return centerBlockPos
}