package io.github.slimeymc.koml

import org.joml.Vector3d

operator fun Vector3d.plus(vector3d: Vector3d) = this.add(vector3d)
operator fun Vector3d.plus(scalar: Double) = this.add(Vector3d(scalar))

operator fun Vector3d.minus(vector3d: Vector3d) = this.sub(vector3d)
operator fun Vector3d.minus(scalar: Double) = this.sub(Vector3d(scalar))

operator fun Vector3d.times(vector3d: Vector3d) = this.mul(vector3d)
operator fun Vector3d.times(scalar: Double) = this.mul(scalar)

operator fun Vector3d.div(vector3d: Vector3d) = this.div(vector3d)
operator fun Vector3d.div(scalar: Double) = this.div(scalar)

fun Vector3d.pow(exponent: Double) = Vector3d(Math.pow(this.x, exponent), Math.pow(this.y, exponent), Math.pow(this.z, exponent))

infix fun Vector3d.`**`(exponent: Double) = this.pow(exponent)
