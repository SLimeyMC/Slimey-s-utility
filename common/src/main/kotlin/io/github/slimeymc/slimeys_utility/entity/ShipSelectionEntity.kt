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
import org.joml.primitives.AABBd
import org.valkyrienskies.core.util.read3FAsNormQuatd

class ShipSelectionEntity(entityType: EntityType<ShipSelectionEntity>, level: Level) : Entity(entityType, level), EntitySpawnExtension {
    var aabb = AABBd()
    var orientation = Quaterniond()
    var prevTickAABB = AABBd()

    override fun defineSynchedData() {}

    override fun readAdditionalSaveData(compound: CompoundTag) {
        if(compound.hasAABBd("shipAABB"))
            this.aabb = compound.getAABBd("shipAABB")!!
        if(compound.hasQuaterniond("shipOrientation"))
            this.orientation = compound.getQuaterniond("shipOrientation")!!
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putAABBd("shipAABB", aabb)
        compound.putQuaterniond("shipOrientation", orientation)
    }

    override fun getAddEntityPacket(): Packet<*> {
        return NetworkManager.createAddEntityPacket(this)
    }

    override fun loadAdditionalSpawnData(buf: FriendlyByteBuf) {
        this.orientation = buf.readQuaterniond()
        this.aabb = buf.readAABBd()
    }

    override fun saveAdditionalSpawnData(buf: FriendlyByteBuf) {
        buf.writeAABBd(this.aabb)
        buf.writeQuaterniond(this.orientation)
    }
}