package io.github.priestoffern.vs_ship_assembler.util


import de.m_marvin.unimat.impl.Quaterniond
import de.m_marvin.univec.impl.Vec3d
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.ShipTeleportData
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl
import java.lang.Math
import java.util.*


class ShipPosition {
    var orientation: Quaterniond
    var position: Vec3d
    var dimension: Optional<String>
    var velocity: Optional<Vec3d> = Optional.empty<Vec3d>()
    var omega: Optional<Vec3d> = Optional.empty<Vec3d>()
    var scale = Optional.empty<Double>()

    constructor(
        orientation: Quaterniond,
        position: Vec3d,
        dimension: String?,
        velocity: Vec3d?,
        omega: Vec3d?,
        scale: Double?
    ) {
        this.orientation = orientation
        this.position = position
        this.dimension = Optional.ofNullable(dimension)
        this.velocity = Optional.ofNullable<Vec3d>(velocity)
        this.omega = Optional.ofNullable<Vec3d>(omega)
        this.scale = Optional.ofNullable(scale)
    }

    constructor(orientation: Quaterniond, position: Vec3d, dimension: String?) {
        this.orientation = orientation
        this.position = position
        this.dimension = Optional.ofNullable(dimension)
    }

    constructor(orientation: Quaterniondc, position: Vector3dc, dimension: String?) : this(
        Quaterniond(
            orientation.x(),
            orientation.y(),
            orientation.z(),
            orientation.w()
        ), Vec3d(position.x(), position.y(), position.z()), dimension
    )

    constructor(transform: ShipTransform) : this(transform.shipToWorldRotation, transform.positionInWorld, null)
    constructor(contraption: Ship) : this(contraption.transform) {
        velocity = Optional.of(Vec3d.fromVec(contraption.velocity))
        omega = Optional.of(Vec3d.fromVec(contraption.omega))
    }

    constructor(contraption: ServerShip, useGeometricCenter: Boolean) : this(contraption) {
        if (useGeometricCenter) {
            val shipBounds = contraption.shipAABB
            val shipCoordCenter: Vec3d = getMiddle(
                Vec3d(shipBounds!!.minX().toDouble(), shipBounds.minY().toDouble(), shipBounds.minZ().toDouble()),
                Vec3d(
                    shipBounds.maxX().toDouble(), shipBounds.maxY().toDouble(), shipBounds.maxZ().toDouble()
                )
            )
            val shipCoordMassCenter: Vec3d =
                Vec3d.fromVec(contraption.inertiaData.centerOfMassInShip).add(Vec3d(0.5, 0.5, 0.5))
            val centerOfMassOffset: Vec3d = toWorldPos(contraption.transform, shipCoordMassCenter)
                .sub(toWorldPos(contraption.transform, shipCoordCenter))
            position.subI(centerOfMassOffset)
        }
    }

    constructor(position: ShipPosition) : this(
        position.getOrientation(),
        position.getPosition(),
        if (position.dimension.isPresent) position.dimension.get() else null,
        if (position.velocity.isPresent) position.velocity.get() else null,
        if (position.omega.isPresent) position.omega.get() else null,
        if (position.scale.isPresent) position.scale.get() else null
    )

    fun toTeleport(ship: ServerShip, useGeometricCenter: Boolean): ShipTeleportData {
        if (useGeometricCenter) {
            val shipBounds = ship.shipAABB
            val shipCoordCenter: Vec3d = getMiddle(
                Vec3d(shipBounds!!.minX().toDouble(), shipBounds.minY().toDouble(), shipBounds.minZ().toDouble()),
                Vec3d(
                    shipBounds.maxX().toDouble(), shipBounds.maxY().toDouble(), shipBounds.maxZ().toDouble()
                )
            )
            val shipCoordMassCenter: Vec3d =
                Vec3d.fromVec(ship.inertiaData.centerOfMassInShip).add(Vec3d(0.5, 0.5, 0.5))
            val centerOfMassOffset: Vec3d = toWorldPos(ship.transform, shipCoordMassCenter)
                .sub(toWorldPos(ship.transform, shipCoordCenter))
            val temp = ShipPosition(this)
            temp.getPosition().addI(centerOfMassOffset)
            return temp.toTeleport()
        }
        return toTeleport()
    }

    fun toTeleport(): ShipTeleportData {
        return ShipTeleportDataImpl(
            positionJOML,
            orientationJOML,
            if (velocity.isPresent) velocity.get().writeTo(Vector3d()) else Vector3d(),
            if (omega.isPresent) omega.get().writeTo(Vector3d()) else Vector3d(),
            if (dimension.isPresent) dimension.get() else null,
            if (scale.isPresent) scale.get() else null
        )
    }

    fun toWorldPosition(transform: ShipTransform) {
        val quat = transform.shipToWorldRotation
        orientation = Quaterniond(quat.x(), quat.y(), quat.z(), quat.w()).mul(
            orientation
        )
        position = toWorldPos(transform, position)
    }

    fun getOrientation(): Quaterniond {
        return orientation
    }

    fun setOrientation(orientation: Quaterniond) {
        this.orientation = orientation
    }

    fun getPosition(): Vec3d {
        return position
    }

    fun setPosition(position: Vec3d) {
        this.position = position
    }

    fun setDimension(dimension: String?) {
        this.dimension = Optional.ofNullable(dimension)
    }

    fun getVelocity(velocity: Vec3d?) {
        this.velocity = Optional.ofNullable<Vec3d>(velocity)
    }

    fun getVelocity(): Optional<Vec3d> {
        return velocity
    }

    fun setOmega(omega: Vec3d?) {
        this.omega = Optional.ofNullable<Vec3d>(omega)
    }

    fun getOmega(): Optional<Vec3d> {
        return omega
    }

    fun setScale(scale: Double?) {
        this.scale = Optional.ofNullable(scale)
    }

    val positionJOML: Vector3d
        get() = Vector3d(position.x, position.y, position.z)
    val positionJOMLi: Vector3i
        get() = Vector3i(
            Math.floor(position.x).toInt(), Math.floor(position.y).toInt(), Math.floor(
                position.z
            ).toInt()
        )
    val orientationJOML: Quaterniondc
        get() = org.joml.Quaterniond(orientation.i, orientation.j, orientation.k, orientation.r)
}