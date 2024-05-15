package io.github.slimeymc.slimeys_utility.entity

import dev.architectury.extensions.network.EntitySpawnExtension
import dev.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import org.joml.Quaterniond
import org.joml.primitives.AABBd

class ShipSelectionEntity(entityType: EntityType<*>, level: Level, var aabb: AABBd, var orientation: Quaterniond) : Entity(entityType, level), EntitySpawnExtension {

    override fun defineSynchedData() {}

    override fun readAdditionalSaveData(compound: CompoundTag) {}

    override fun addAdditionalSaveData(compound: CompoundTag) {}

    override fun getAddEntityPacket(): Packet<*> {
        //return NetworkManager.createAddEntityPacket(ShipSelectionEntity)
        TODO("Not yet implemented grr")
    }

    override fun saveAdditionalSpawnData(buf: FriendlyByteBuf?) {
        TODO("Not yet implemented")
    }

    override fun loadAdditionalSpawnData(buf: FriendlyByteBuf?) {
        TODO("Not yet implemented")
    }
}