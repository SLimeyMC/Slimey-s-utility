package io.github.slimeymc.koml

import org.joml.Matrix4d
import org.joml.Vector3d
import org.joml.Vector4d

// a 3x4 Matrix that define rotation, scaling, shearing (basis) and translation
// It's basically just affineMatrix but without the last row
data class Transform3d(val basis: Basis3d, val origin: Vector3d) {
    val affineMatrix: Matrix4d
        get() = Matrix4d(
                basis.i.x, basis.i.y, basis.i.z, origin.x,
                basis.j.x, basis.j.y, basis.j.z, origin.y,
                basis.k.x, basis.k.y, basis.k.z, origin.z,
                0.0, 0.0, 0.0, 1.0
        )

    constructor(basis: Basis3d, originX: Double, originY: Double, originZ: Double) :
            this(basis, Vector3d(originX, originY, originZ))

    fun transform(point: Vector3d): Vector3d = affineMatrix.transformPosition(point)

    fun isHomogeneous(): Boolean {
        return origin.x != 0.0 || origin.y != 0.0 || origin.z != 0.0
    }

    fun invert(): Transform3d {
        val invBasis = basis.invert()
        val invOrigin = invBasis.transform(origin)
        return Transform3d(invBasis, invOrigin)
    }

    fun times(other: Transform3d): Transform3d {
        val newBasis = Basis3d((basis * other.basis.i), (basis * other.basis.j), (basis * other.basis.k))
        val newOrigin = transform(other.origin)
        return Transform3d(newBasis, newOrigin)
    }

    fun decompose(): Pair<Basis3d, Vector3d> {
        return Pair(basis, origin)
    }
}