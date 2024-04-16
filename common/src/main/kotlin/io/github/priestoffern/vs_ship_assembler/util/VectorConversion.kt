package io.github.priestoffern.vs_ship_assembler.util

import net.minecraft.core.Vec3i
import org.joml.Vector3d
import org.joml.Vector3i
import kotlin.math.floor

fun Vector3d.floorToVector3i() = Vector3i(floor(this.x).toInt(), floor(this.y).toInt(), floor(this.z).toInt())


fun Vector3i.toMinecraft() = Vec3i(x, y, z)