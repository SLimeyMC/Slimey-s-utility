package io.github.slimeymc.slimeys_utility.util

import net.minecraft.network.FriendlyByteBuf
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc

fun FriendlyByteBuf.writeVector3d(vector3d: Vector3dc) =
    with(vector3d) {
        writeLongArray(LongArray(3).apply {
            set(0, z().toRawBits())
            set(1, y().toRawBits())
            set(2, x().toRawBits())
        })
    }
fun FriendlyByteBuf.readVector3d(): Vector3d {
    val data = readLongArray(LongArray(6)).iterator()
    return Vector3d().apply {
        x = Double.fromBits(data.next())
        y = Double.fromBits(data.next())
        z = Double.fromBits(data.next())
    }
}

fun FriendlyByteBuf.writeAABBd(aabBd: AABBdc) =
    with(aabBd) {
        writeLongArray(LongArray(6).apply {
            set(0, minX().toRawBits())
            set(1, minY().toRawBits())
            set(2, minZ().toRawBits())
            set(3, maxX().toRawBits())
            set(4, maxY().toRawBits())
            set(5, maxZ().toRawBits())
        })
    }
fun FriendlyByteBuf.readAABBd(): AABBd {
    val data = readLongArray(LongArray(6)).iterator()
    return AABBd().apply {
        minX = Double.fromBits(data.next())
        minY = Double.fromBits(data.next())
        minZ = Double.fromBits(data.next())
        maxX = Double.fromBits(data.next())
        maxY = Double.fromBits(data.next())
        maxZ = Double.fromBits(data.next())
    }
}

fun FriendlyByteBuf.writeQuaterniond(quaterniond: Quaterniondc) =
    with(quaterniond) {
        writeLongArray(LongArray(4).apply {
            set(0, x().toRawBits())
            set(1, y().toRawBits())
            set(2, z().toRawBits())
            set(3, w().toRawBits())
        })
    }
fun FriendlyByteBuf.readQuaterniond(): Quaterniond {
    val data = readLongArray(LongArray(4)).iterator()
    return Quaterniond().apply {
        x = Double.fromBits(data.next())
        y = Double.fromBits(data.next())
        z = Double.fromBits(data.next())
        w = Double.fromBits(data.next())
    }
}

