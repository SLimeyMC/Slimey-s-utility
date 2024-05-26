package io.github.slimeymc.slimeys_utility.entity

import dev.architectury.extensions.network.EntitySpawnExtension
import dev.architectury.networking.NetworkManager
import io.github.slimeymc.slimeys_utility.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.primitives.AABBd
import org.joml.primitives.AABBi
import org.valkyrienskies.core.util.readAABBi
import org.valkyrienskies.core.util.writeAABBi
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.util.getVector3d
import org.valkyrienskies.mod.util.putVector3d

class ShipSelectionEntity(entityType: EntityType<ShipSelectionEntity>, level: Level) : Entity(entityType, level), EntitySpawnExtension {
    var aabb = AABBd()
    var orientation = Quaterniond()
    var targetPosition = Vector3d()
    var targetAABB = AABBi()
    var targetOrientation = Quaterniond()

    var acceleration = 0.2

    override fun tick() {
        setPos(position().toJOML().lerp(targetPosition, acceleration).toMinecraft())
        aabb = AABBd(
           Vector3d(aabb.minX, aabb.minY, aabb.minZ)
               .lerp(Vector3d(targetAABB.minX.toDouble(), targetAABB.minY.toDouble(), targetAABB.minZ.toDouble()), acceleration),
           Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)
               .lerp(Vector3d(targetAABB.maxX.toDouble(), targetAABB.maxY.toDouble(), targetAABB.maxZ.toDouble()), acceleration),
            )
        orientation = orientation.slerp(targetOrientation, acceleration)
    }

    override fun defineSynchedData() {

    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        targetPosition = Vector3d(compound.getVector3d("shipPosition"))
        targetAABB = AABBi(compound.getAABBi("shipAABB"))
        targetOrientation = Quaterniond(compound.getQuaterniond("shipOrientation"))
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putVector3d("shipPosition", targetPosition)
        compound.putAABBi("shipAABB", targetAABB)
        compound.putQuaterniond("shipOrientation", targetOrientation)
    }

    override fun getAddEntityPacket(): Packet<*> {
        return NetworkManager.createAddEntityPacket(this)
    }

    override fun loadAdditionalSpawnData(buf: FriendlyByteBuf) {
        this.targetPosition = buf.readVector3d()
        this.targetAABB = buf.readAABBi()
        this.targetOrientation = buf.readQuaterniond()
    }

    override fun saveAdditionalSpawnData(buf: FriendlyByteBuf) {
        buf.writeVector3d(targetPosition)
        buf.writeAABBi(targetAABB)
        buf.writeQuaterniond(targetOrientation)
    }
}