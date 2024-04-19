package io.github.priestoffern.vs_ship_assembler.items

import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.mod.common.world.clipIncludeShips

open class RaycastableItem(properties: Properties) : Item(properties) {
    protected fun clipFromPlayer(level: Level, player: Player, fluidMode: ClipContext.Fluid): ClipContext {
        val yawRad: Float = player.yRot * Mth.DEG_TO_RAD
        val pitchRad: Float = player.xRot * Mth.DEG_TO_RAD
        val eyePosition: Vec3 = player.eyePosition

        val directionX: Double  = Mth.cos(yawRad - Mth.PI) * -Mth.cos(pitchRad).toDouble()
        val directionY: Double  = Mth.sin(pitchRad).toDouble()
        val directionZ: Double  = Mth.sin(yawRad - Mth.PI) * -Mth.cos(pitchRad).toDouble()

        val rayEnd: Vec3 = eyePosition.add(directionX, directionY, directionZ).multiply(5.0, 5.0, 5.0)

        return ClipContext(eyePosition, rayEnd, ClipContext.Block.OUTLINE, fluidMode, player)
    }
}