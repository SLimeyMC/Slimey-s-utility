package io.github.slimeymc.koml

import org.joml.Matrix4d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector4d

// a 3x4 Matrix that define rotation, scaling, shearing (basis) and translation
// It's basically just affineMatrix but without the last row
data class Transform3d(var basis: Basis3d, var origin: Vector3d) {
    val affineMatrix: Matrix4d
        get() = Matrix4d(
                basis.i.x, basis.i.y, basis.i.z, origin.x,
                basis.j.x, basis.j.y, basis.j.z, origin.y,
                basis.k.x, basis.k.y, basis.k.z, origin.z,
                0.0, 0.0, 0.0, 1.0
        )

    val determinant: Double
        get() = this.basis.determinant

    var scale: Vector3d
        get() = this.basis.scale
        set(value) { this.basis.scale = value }

    var rotationQuaternion: Quaterniond
        get() = this.basis.rotationQuaternion
        set(value) { this.basis.rotationQuaternion = value }
    var rotationEulerXYZ: Vector3d
        get() = this.basis.rotationEulerXYZ
        set(value) { this.basis.rotationEulerXYZ = value }
    var rotationEulerZYX: Vector3d
        get() = this.basis.rotationEulerZYX
        set(value) { this.basis.rotationEulerZYX = value }

    constructor() :
            this(Basis3d(), Vector3d())

    constructor(basis: Basis3d) :
            this(basis, Vector3d())

    constructor(origin: Vector3d) :
            this(Basis3d(), origin)

    constructor(originX: Double, originY: Double, originZ: Double) :
            this(Basis3d(), originX, originY, originZ)

    constructor(basis: Basis3d, originX: Double, originY: Double, originZ: Double) :
            this(basis, Vector3d(originX, originY, originZ))

    fun transform(point: Vector3d): Vector3d = affineMatrix.transformPosition(point)

    fun isHomogeneous(): Boolean = origin.x != 0.0 || origin.y != 0.0 || origin.z != 0.0

    fun interpolateWith(other: Transform3d, t: Double): Transform3d = Transform3d(
        Basis3d(this.rotationQuaternion.slerp(other.rotationQuaternion, t)).scale(this.scale.lerp(other.scale, t)),
        this.origin.lerp(other.origin, t)
    )

    fun invert(): Transform3d {
        val transposedBasis = basis.transpose()
        return Transform3d(transposedBasis, transposedBasis.transform(Vector3d() - origin))
    }

    fun times(other: Transform3d): Transform3d = Transform3d(
        Basis3d((basis * other.basis.i), (basis * other.basis.j), (basis * other.basis.k)),
        transform(other.origin)
    )
}