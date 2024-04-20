package io.github.priestoffern.vs_ship_assembler.physicify

import io.github.priestoffern.vs_ship_assembler.ship.ShipData
import io.github.priestoffern.vs_ship_assembler.util.*
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
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
import org.valkyrienskies.mod.common.vsCore
import org.valkyrienskies.mod.common.world.clipIncludeShips
import java.util.*

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
    val shipWorldPos: Vector3d = structureCornerMin.toJOMLD().add(structureCornerMax.toJOMLD()).div(2.0)
    val shipData = ShipData(Quaterniond(0.0, 0.0, 0.0, 1.0), shipWorldPos, null)
    val shipBlockPos: BlockPos = createShipAt(level, shipData, scale)
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

    if (ship.inertiaData.mass == 0.0) {
        vsCore.deleteShips(level.shipObjectWorld, listOf(ship))
        LOGGER.severe("Created ship has mass of 0! Aborting the operation and deleting the ship!")
    }

    teleportShip(level, ship, shipData)

    return ship
}

fun scaleShip(level: ServerLevel, ship: ServerShip, scale: Double) {
    val shipData = ShipData(ship)
    // Start checking if ship is hitting something (not inluding it's parent and children and it's corner)
    val rayXStart = Vec3(ship.worldAABB.maxX() + 1.0, shipData.position.y, shipData.position.z)
    val rayXEnd = Vec3(ship.worldAABB.minX() - 1.0, shipData.position.y, shipData.position.z)
    val rayYStart = Vec3(shipData.position.x, ship.worldAABB.maxY() + 1.0, shipData.position.z)
    val rayYEnd = Vec3(shipData.position.x, ship.worldAABB.minY() - 1.0, shipData.position.z)
    val rayZStart = Vec3(shipData.position.x, shipData.position.y, ship.worldAABB.maxZ() + 1.0)
    val rayZEnd = Vec3(shipData.position.x, shipData.position.y, ship.worldAABB.minZ() - 1.0)

    val shipPosition = shipData.position.toMinecraft()

    shipData.scale = Optional.of(ship.transform.shipToWorldScaling.mul(scale, Vector3d()).x)

    val raycastTo = fun(position: Vec3): Double {
        return level.clipIncludeShips(
            ClipContext(shipPosition, position, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, null)
        ).location.distanceTo(shipPosition)
    }

    // If it hit move the ship location further away from the BlockHitResult
    val pushDistance = Vector3d(
        raycastTo(rayXEnd) - raycastTo(rayXStart),
        raycastTo(rayYEnd) - raycastTo(rayYStart),
        raycastTo(rayZEnd) - raycastTo(rayZStart)
    )

    shipData.velocity = Optional.of(pushDistance.mul(scale).sub(pushDistance))

    vsCore.scaleShip(level.shipObjectWorld, ship, scale)
    //teleportShip(level, ship, shipData)
}

fun createShipAt(level: ServerLevel, shipData: ShipData, scale: Double): BlockPos {

    // Get parent ship (if existing)
    val parentShip: Ship? =
        level.getShipManagingPos(shipData.position)

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
    val centerBlockPos = BlockPos(newShip.shipAABB!!.center(t).toMinecraft())
    level.setBlock(centerBlockPos, Blocks.STONE.defaultBlockState(), 3)

    // Teleport ship to final destination
    level.server.shipObjectWorld
        .teleportShip(newShip as ServerShip, shipData.toShipTeleportData())
    return centerBlockPos
}

private fun teleportShip(level: ServerLevel, ship: ServerShip, shipData: ShipData) {
    level.server.shipObjectWorld
        .teleportShip(ship, shipData.toShipTeleportData())
}