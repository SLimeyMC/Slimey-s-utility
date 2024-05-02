package io.github.slimeyar.slimeys_utility.items

import io.github.slimeyar.slimeys_utility.SlimeysUtilityMod.RENDER_TYPE
import io.github.slimeyar.slimeys_utility.client.renderer.Renderer
import io.github.slimeyar.slimeys_utility.client.renderer.RenderingData
import io.github.slimeyar.slimeys_utility.client.renderer.SelectionZoneRenderer
import io.github.slimeyar.slimeys_utility.util.toFloat
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.world.clipIncludeShips
import java.awt.Color

open class ShipSelectingItem(properties: Properties) : RaycastableItem(properties) {
    private var selectionZone: RenderingData? = null
    var selectedShip: Ship? = null

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        super.inventoryTick(stack, level, entity, slotId, isSelected)
        val blockHitResult = level.clipIncludeShips(clipFromPlayer(level, entity as Player, ClipContext.Fluid.ANY))
        selectedShip = level.getShipManagingPos(blockHitResult.blockPos)

        if(selectedShip != null) {
            if (selectionZone != null) Renderer.removeRender(selectionZone!!)
            selectionZone = null

            val SZ = SelectionZoneRenderer(
                Vector3d(selectedShip!!.worldAABB.minX(), selectedShip!!.worldAABB.minY(), selectedShip!!.worldAABB.minZ()).toFloat(),
                Vector3d(selectedShip!!.worldAABB.maxX(), selectedShip!!.worldAABB.maxY(), selectedShip!!.worldAABB.maxZ()).toFloat(),
                Color.GREEN, 0, RENDER_TYPE
            )
            selectionZone = Renderer.addRender(SZ)
        }
    }
}