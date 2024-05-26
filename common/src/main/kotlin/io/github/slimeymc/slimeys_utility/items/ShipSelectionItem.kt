package io.github.slimeymc.slimeys_utility.items

import dev.architectury.networking.NetworkManager
import io.github.slimeymc.slimeys_utility.SlimeysUtilityEntities
import io.github.slimeymc.slimeys_utility.SlimeysUtilityEntities.SHIP_SELECTION_ENTITY
import io.github.slimeymc.slimeys_utility.SlimeysUtilityMod.RENDER_TYPE
import io.github.slimeymc.slimeys_utility.SlimeysUtilityNetworkings
import io.github.slimeymc.slimeys_utility.client.renderer.Renderer
import io.github.slimeymc.slimeys_utility.client.renderer.RenderingData
import io.github.slimeymc.slimeys_utility.client.renderer.SelectionZoneRenderer
import io.github.slimeymc.slimeys_utility.entity.ShipSelectionEntity
import io.github.slimeymc.slimeys_utility.util.*
import io.netty.buffer.Unpooled
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.primitives.AABBd
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.util.writeAABBi
import org.valkyrienskies.core.util.writeQuatd
import org.valkyrienskies.core.util.writeVec3d
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import java.awt.Color

open class ShipSelectionItem(properties: Properties) : RaycastableItem(properties) {
    private var shipSelectionEntity: ShipSelectionEntity? = null
    var selectedShip: Ship? = null

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        super.inventoryTick(stack, level, entity, slotId, isSelected)
        val blockHitResult = level.clipIncludeShips(
            clipFromPlayer(level, entity as Player, ClipContext.Fluid.ANY), false)
        selectedShip = level.getShipManagingPos(blockHitResult.blockPos)

        if(selectedShip != null && isSelected) {
            val shipAABB = AABBi(selectedShip!!.shipAABB)
            val shipOrientation = Quaterniond(selectedShip!!.transform.shipToWorldRotation)
            if (shipSelectionEntity == null) {
                val packet = CompoundTag()
                packet.putAABBi("ShipAABB", shipAABB)
                packet.putQuaterniond("ShipOrientation", shipOrientation)
                SHIP_SELECTION_ENTITY.get().spawn(
                    level as ServerLevel, packet, null, null,
                    shipAABB.center(Vector3d()).floorToBlockPos(),
                    MobSpawnType.TRIGGERED, false, false
                )
            } else {
                // TODO Slowly interpolate
                val buf = FriendlyByteBuf(Unpooled.buffer())
                buf.writeVector3d(selectedShip!!.worldAABB.center(Vector3d()))
                buf.writeAABBi(shipAABB)
                buf.writeQuaterniond(shipOrientation)
                shipSelectionEntity!!.loadAdditionalSpawnData(buf)
            }
        } else if(shipSelectionEntity != null) {
            shipSelectionEntity!!.kill()
            shipSelectionEntity = null
        }
    }
}