package io.github.slimeymc.koml

import org.joml.*
import org.lwjgl.system.MathUtil
import kotlin.math.cos
import kotlin.math.sin

// Just Matrix3 that's it
data class Basis3d(internal var basis: Matrix3d) {
    val i: Vector3d
        get() = Vector3d(basis.m00, basis.m01, basis.m02)
    val j: Vector3d
        get() = Vector3d(basis.m10, basis.m11, basis.m12)
    val k: Vector3d
        get() = Vector3d(basis.m20, basis.m21, basis.m22)

    val x: Vector3d
        get() = Vector3d(basis.m00, basis.m10, basis.m20)
    val y: Vector3d
        get() = Vector3d(basis.m01, basis.m11, basis.m21)
    val z: Vector3d
        get() = Vector3d(basis.m02, basis.m12, basis.m22)

    val affineMatrix: Matrix4d
        get() = Matrix4d(this.i.x, this.i.y, this.i.z, 0.0,
            this.j.x, this.j.y, this.j.z, 0.0,
            this.k.x, this.k.y, this.k.z, 0.0,
            0.0, 0.0, 0.0, 1.0)

    val determinant: Double
        get() = this.i.x * (this.j.y * this.k.z - this.k.y * this.j.z) -
                this.j.x * (this.i.y * this.k.z - this.k.y * this.i.z) +
                this.k.x * (this.i.y * this.j.z - this.j.y * this.i.z)

    var scale: Vector3d
        get() = this.basis.getScale(Vector3d())
        set(value) { this.scale(value) }

    var rotationQuaternion: Quaterniond
        get() = this.basis.getNormalizedRotation(Quaterniond())
        set(value) {}
    var rotationEulerXYZ: Vector3d
        get() = this.basis.getEulerAnglesXYZ(Vector3d())
        set(value) { this.setEuler(value, EulerOrder.XYZ) }
    var rotationEulerZYX: Vector3d
        get() = this.basis.getEulerAnglesZYX(Vector3d())
        set(value) { this.setEuler(value, EulerOrder.ZYX) }


    companion object {
        val IDENTITY = Basis3d(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0)
        val FLIP_X = Basis3d(-1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0)
        val FLIP_Y = Basis3d(1.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 1.0)
        val FLIP_Z = Basis3d(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, -1.0)
    }

    constructor(basis: Basis3d) : this(basis.basis)

    constructor(ix: Double, iy: Double, iz: Double, jx: Double, jy: Double, jz: Double, kx: Double, ky: Double, kz: Double) :
            this(Vector3d(ix, iy, iz), Vector3d(jx, jy, jz), Vector3d(kx, ky, kz))

    constructor(i: Vector3dc, j: Vector3dc, k: Vector3dc) : this(Matrix3d().setRow(0, i).setRow(1, j).setRow(2, k))

    fun isSheared(): Boolean = (i.dot(j) > EPSILON || i.dot(k) > EPSILON || j.dot(k) > EPSILON)
    fun isOrthogonal(): Boolean = !isSheared()

    fun normalize(): Basis3d = Basis3d(i.normalize(), j.normalize(), k.normalize())
    fun orthonormalize(): Basis3d {
        val u1 = i.normalize()
        val u2 = (u1 * j - u1.dot(j)).normalize()
        val u3 = (u1 * k - (u1.dot(k)) - (u2 * u2.dot(k))).normalize()
        return Basis3d(u1, u2, u3)
    }

    fun scale(vector: Vector3d): Basis3d = Basis3d(i * vector.x, j * vector.y, k * vector.z)
    fun scale(factor: Double): Basis3d = Basis3d(i * factor, j * factor, k * factor)

    fun invert(): Basis3d {
        val inv = Matrix4d(i.x, i.y, i.z, 0.0,
            j.x, j.y, j.z, 0.0,
            k.x, k.y, k.z, 0.0,
            0.0, 0.0, 0.0, 1.0)
        inv.invertAffine()
        return Basis3d(
            inv.m00(), inv.m01(), inv.m02(),
            inv.m10(), inv.m11(), inv.m12(),
            inv.m20(), inv.m21(), inv.m22()
        )
    }

    fun transform(point: Vector3d): Vector3d = this.basis.transform(point)

    fun reflect(normal: Vector3d): Basis3d = Basis3d(this.basis.reflect(normal))

    fun reflectPoint(point: Vector3d, normal: Vector3d): Vector3d =
        transform(normal * 2.0 * point.dot(normal) - point)

    fun tdotx(v: Vector3d): Double =
        this.i.x * v.x + this.j.x * v.y + this.k.x * v.z


    fun tdoty(v: Vector3d): Double =
        this.i.y * v.x + this.j.y * v.y + this.k.y * v.z


    fun tdotz(v: Vector3d): Double =
        this.i.z * v.x + this.j.z * v.y + this.k.z * v.z


    fun transpose(): Basis3d =
        Basis3d(i.x, j.x, k.x,
            i.y, j.y, k.y,
            i.z, j.z, k.z
        )

    operator fun plus(matrix: Basis3d) = Basis3d(this.i + matrix.i, this.j + matrix.j, this.k + matrix.k)

    operator fun minus(matrix: Basis3d) = Basis3d(this.i - matrix.i, this.j - matrix.j, this.k - matrix.k)

    operator fun times(matrix: Basis3d) = Basis3d(
        matrix.tdotx(this.i), matrix.tdoty(this.i), matrix.tdotz(this.i),
        matrix.tdotx(this.j), matrix.tdoty(this.j), matrix.tdotz(this.j),
        matrix.tdotx(this.k), matrix.tdoty(this.k), matrix.tdotz(this.k)
    )
    operator fun times(vector: Vector3d) = this.transform(vector)

    operator fun times(scalar: Double) = this.scale(scalar)

    internal fun setEuler(euler: Vector3d, order: EulerOrder = EulerOrder.YXZ) {
        var c = cos(euler.x)
        var s = sin(euler.x)

        val xmat = Basis3d(1.0, 0.0, 0.0, 0.0, c, -s, 0.0, s, c)

        c = cos(euler.y)
        s = sin(euler.y)
        val ymat = Basis3d(c, 0.0, s, 0.0, 1.0, 0.0, -s, 0.0, c)

        c = cos(euler.z)
        s = sin(euler.z)
        val zmat = Basis3d(c, -s, 0.0, s, c, 0.0, 0.0, 0.0, 1.0)

        when (order) {
            EulerOrder.XYZ -> Basis3d(xmat * (ymat * zmat))
            EulerOrder.XZY -> Basis3d(xmat * zmat * ymat)
            EulerOrder.YXZ -> Basis3d(ymat * xmat * zmat)
            EulerOrder.YZX -> Basis3d(ymat * zmat * xmat)
            EulerOrder.ZXY -> Basis3d(zmat * xmat * ymat)
            EulerOrder.ZYX -> Basis3d(zmat * ymat * xmat)
        }
    }
}