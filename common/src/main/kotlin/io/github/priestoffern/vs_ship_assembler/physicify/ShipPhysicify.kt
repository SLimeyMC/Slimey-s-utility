package io.github.priestoffern.vs_ship_assembler.physicify

import io.github.priestoffern.vs_ship_assembler.util.RelocateLevel
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import org.joml.Math
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.vsCore

fun physicifyBlocks(level: ServerLevel, blocks: DenseBlockPosSet, scale: Double): ServerShip {
    if (blocks.isEmpty()) throw IllegalArgumentException()

    // Find the bound of the ship to be physicified
    var structureCornerMin = BlockPos(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
    var structureCornerMax = BlockPos(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
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
    val shipWorldPos: Vector3i = structureCornerMin.toJOML().add(structureCornerMax.toJOML()).div(2)



    val newShip: Ship = (level as ServerLevel).server.shipObjectWorld
        .createNewShipAtBlock(shipWorldPos, false, scale, level.dimensionId)

    // Stone for safety reasons

    val ShipPos = newShip.worldToShip.transformPosition(Vector3d(shipWorldPos.x.toDouble(),shipWorldPos.y.toDouble(),shipWorldPos.z.toDouble()))
    val shipBlockPos = BlockPos(shipWorldPos.x.toInt(),shipWorldPos.y.toInt(),shipWorldPos.z.toInt())
    val ship: ServerShip = level.getShipManagingPos(shipBlockPos)!!


    // Make a class to help with moving out the block
    val relocateLevel = RelocateLevel(level)


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


    (level as ServerLevel).server.shipObjectWorld
        .teleportShip(newShip as ServerShip, ShipTeleportDataImpl(Vector3d(shipWorldPos.x.toDouble(),shipWorldPos.y.toDouble(),shipWorldPos.z.toDouble())))

    return ship
}

fun scaleShip(level: ServerLevel, ship: ServerShip, scale: Double) {
    // Start checking if ship is hitting something (not inluding it's parent and children and it's corner)
//    val rayXStart = Vec3(ship.worldAABB.maxX() + 1.0, ship.transform.positionInWorld.y, ship.transform.positionInWorld.z)
//    val rayXEnd = Vec3(ship.worldAABB.minX() - 1.0, ship.transform.positionInWorld.y, ship.transform.positionInWorld.z)
//    val rayYStart = Vec3(ship.transform.positionInWorld.x, ship.worldAABB.maxY() + 1.0, ship.transform.positionInWorld.z)
//    val rayYEnd = Vec3(ship.transform.positionInWorld.x, ship.worldAABB.minY() - 1.0, ship.transform.positionInWorld.z)
//    val rayZStart = Vec3(ship.transform.positionInWorld.x, ship.transform.positionInWorld.y, ship.worldAABB.maxZ() + 1.0)
//    val rayZEnd = Vec3(ship.transform.positionInWorld.x, ship.transform.positionInWorld.y, ship.worldAABB.minZ() - 1.0)
//
//    val shipPosition = ship.transform.positionInWorld.toMinecraft()
//
//
//    val raycastTo = fun(position: Vec3): Double {
//        return level.clipIncludeShips(
//            ClipContext(shipPosition, position, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, null)
//        ).location.distanceTo(shipPosition)
//    }
//
//    // If it hit move the ship location further away from the BlockHitResult
//    val pushDistance = Vector3d(
//        raycastTo(rayXEnd) - raycastTo(rayXStart),
//        raycastTo(rayYEnd) - raycastTo(rayYStart),
//        raycastTo(rayZEnd) - raycastTo(rayZStart)
//    )
//
//    ship.velocity = Optional.of(pushDistance.mul(scale).sub(pushDistance))

    vsCore.scaleShip(level.shipObjectWorld, ship, scale)
    //teleportShip(level, ship, shipData)
}

