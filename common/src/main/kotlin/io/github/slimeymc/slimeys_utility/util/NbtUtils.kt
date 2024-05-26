package io.github.slimeymc.slimeys_utility.util

import net.minecraft.nbt.CompoundTag
import org.joml.Quaterniond
import org.joml.Quaterniondc

import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic

fun CompoundTag.putAABBi(prefix: String, aabBd: AABBic) =
    with(aabBd) {
        putInt(prefix + "x", minX())
        putInt(prefix + "y", minY())
        putInt(prefix + "z", minZ())
        putInt(prefix + "X", maxX())
        putInt(prefix + "Y", maxY())
        putInt(prefix + "Z", maxZ())
    }
fun CompoundTag.getAABBi(prefix: String): AABBic? =
    if (hasAABBi(prefix)) null
    else AABBi(
            this.getInt(prefix + "x"),
            this.getInt(prefix + "y"),
            this.getInt(prefix + "z"),
            this.getInt(prefix + "X"),
            this.getInt(prefix + "Y"),
            this.getInt(prefix + "Z")
        )
fun CompoundTag.hasAABBi(prefix: String): Boolean =
            !prefix.contains(prefix + "x") ||
            !prefix.contains(prefix + "y") ||
            !prefix.contains(prefix + "z") ||
            !prefix.contains(prefix + "X") ||
            !prefix.contains(prefix + "Y") ||
            !prefix.contains(prefix + "Z")

fun CompoundTag.putAABBd(prefix: String, aabBd: AABBdc) =
    with(aabBd) {
        putDouble(prefix + "x", minX())
        putDouble(prefix + "y", minY())
        putDouble(prefix + "z", minZ())
        putDouble(prefix + "X", maxX())
        putDouble(prefix + "Y", maxY())
        putDouble(prefix + "Z", maxZ())
    }
fun CompoundTag.getAABBd(prefix: String): AABBdc? =
    if (hasAABBd(prefix)) null
    else AABBd(
            this.getDouble(prefix + "x"),
            this.getDouble(prefix + "y"),
            this.getDouble(prefix + "z"),
            this.getDouble(prefix + "X"),
            this.getDouble(prefix + "Y"),
            this.getDouble(prefix + "Z")
        )
fun CompoundTag.hasAABBd(prefix: String): Boolean =
            !prefix.contains(prefix + "x") ||
            !prefix.contains(prefix + "y") ||
            !prefix.contains(prefix + "z") ||
            !prefix.contains(prefix + "X") ||
            !prefix.contains(prefix + "Y") ||
            !prefix.contains(prefix + "Z")

fun CompoundTag.putQuaterniond(prefix: String, quaterniond: Quaterniondc) =
    with(quaterniond) {
        putDouble(prefix + "x", x())
        putDouble(prefix + "y", y())
        putDouble(prefix + "z", z())
        putDouble(prefix + "w", w())
    }
fun CompoundTag.getQuaterniond(prefix: String): Quaterniondc? =
    if (hasQuaterniond(prefix))null
    else Quaterniond(
            this.getDouble(prefix + "x"),
            this.getDouble(prefix + "y"),
            this.getDouble(prefix + "z"),
            this.getDouble(prefix + "w")
        )
fun CompoundTag.hasQuaterniond(prefix: String): Boolean =
            !prefix.contains(prefix + "x") ||
            !prefix.contains(prefix + "y") ||
            !prefix.contains(prefix + "z") ||
            !prefix.contains(prefix + "w")