package io.github.priestoffern.vs_ship_assembler.util

import org.joml.Vector3d
import org.joml.Vector3f
import org.joml.Vector3i
import kotlin.math.floor

fun Vector3d.floorToJOMLI() = Vector3i(floor(this.x).toInt(), floor(this.y).toInt(), floor(this.z).toInt())


fun Vector3d.toFloat() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

fun iterateCorners(min: Vector3f, max: Vector3f): Iterable<Vector3f> {
    return object : Iterable<Vector3f> {
        override fun iterator(): Iterator<Vector3f> {
            var x = min.x
            var y = min.y
            var z = min.z
            var hasNext = true

            return object : Iterator<Vector3f> {
                override fun hasNext(): Boolean {
                    return hasNext
                }

                override fun next(): Vector3f {
                    val corner = Vector3f(x, y, z)
                    // Increment coordinates systematically to iterate through all corners
                    if (x < max.x) {
                        x++
                    } else if (y < max.y) {
                        x = min.x
                        y++
                    } else if (z < max.z) {
                        x = min.x
                        y = min.y
                        z++
                    } else {
                        hasNext = false
                    }
                    return corner
                }
            }
        }
    }
}