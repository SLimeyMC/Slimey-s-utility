package io.github.priestoffern.vs_ship_assembler.items

import io.github.priestoffern.vs_ship_assembler.rendering.Renderer
import io.github.priestoffern.vs_ship_assembler.rendering.RenderingData
import io.github.priestoffern.vs_ship_assembler.rendering.SelectionZoneRenderer
import io.github.priestoffern.vs_ship_assembler.util.toFloat
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.world.clipIncludeShips
import java.awt.Color

open class ShipSelectingItem(properties: Properties) : RaycastableItem(properties) {
    private var selectionZone: RenderingData? = null

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        super.inventoryTick(stack, level, entity, slotId, isSelected)
        val blockHitResult = level.clipIncludeShips(clipFromPlayer(level, entity as Player, ClipContext.Fluid.ANY))
        val shipResult = level.getShipManagingPos(blockHitResult.blockPos)

        if(shipResult != null) {
            if (selectionZone != null) Renderer.removeRender(selectionZone!!)
            selectionZone = null

            val SZ = SelectionZoneRenderer(
                Vector3d(shipResult.worldAABB.minX(), shipResult.worldAABB.minY(), shipResult.worldAABB.minZ()).toFloat(),
                Vector3d(shipResult.worldAABB.maxX(), shipResult.worldAABB.maxY(), shipResult.worldAABB.maxZ()).toFloat(),
                Color.GREEN
            )
            selectionZone = Renderer.addRender(SZ)
        }
    }
}