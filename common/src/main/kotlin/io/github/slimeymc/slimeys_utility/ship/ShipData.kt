package io.github.slimeymc.slimeys_utility.ship


import io.github.slimeymc.koml.*
import org.joml.*
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.ShipTeleportData
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl


class ShipData {
    var orientation: Quaterniond
    var position: Vector3d
    var dimension: String? = null
    var velocity = Vector3d()
    var omega = Vector3d()
    var scale = 0.0

    constructor(
        orientation: Quaterniond,
        position: Vector3d,
        dimension: String?,
        velocity: Vector3d?,
        omega: Vector3d?,
        scale: Double?
    ) {
        this.orientation = orientation
        this.position = position
        if(dimension != null) this.dimension = dimension
        if(velocity != null) this.velocity = velocity
        if(omega != null) this.omega = omega
        if(scale != null) this.scale = scale
    }

    constructor(orientation: Quaterniond, position: Vector3d, dimension: String?) {
        this.orientation = orientation
        this.position = position
        if(dimension != null) this.dimension = dimension
    }

    constructor(orientation: Quaterniondc, position: Vector3dc, dimension: String?) : this(
        Quaterniond(orientation),
        Vector3d(position), dimension
    )

    constructor(transform: ShipTransform) : this(transform.shipToWorldRotation, transform.positionInWorld, null)

    constructor(ship: Ship) : this(ship.transform) {
        this.velocity = Vector3d(ship.velocity)
        this.omega = Vector3d(ship.omega)
    }

    constructor(ship: ServerShip, useGeometricCenter: Boolean) : this(ship) {
        if (useGeometricCenter) {
            val shipBound = ship.shipAABB
            val shipCoordBoundCenter = Vector3d()
                shipBound?.center(shipCoordBoundCenter)
            val shipCoordMassCenter: Vector3d =
                Vector3d(ship.inertiaData.centerOfMassInShip) + Vector3d(0.5)
            val centerOfMassOffset: Vector3d = transformPosition(ship.transform, shipCoordMassCenter)
                .sub(transformPosition(ship.transform, shipCoordBoundCenter))
            position.sub(centerOfMassOffset)
        }
    }

    constructor(position: ShipData) : this(
        position.orientation,
        position.position,
        if (position.dimension == null) position.dimension else null,
        position.velocity,
        position.omega,
        position.scale
    )

    // Use Geometric center idk
    fun toShipTeleportData(ship: ServerShip): ShipTeleportData {
        val shipBound = ship.shipAABB
        val shipCoordBoundCenter = Vector3d()
        shipBound?.center(shipCoordBoundCenter)
        val shipCoordMassCenter: Vector3d =
            Vector3d(ship.inertiaData.centerOfMassInShip).add(Vector3d(0.5))
        val centerOfMassOffset: Vector3d = transformPosition(ship.transform, shipCoordMassCenter)
            .sub(transformPosition(ship.transform, shipCoordBoundCenter))
        val temp = ShipData(this)
        temp.position.add(centerOfMassOffset)
        return temp.toShipTeleportData()
    }

    fun toShipTeleportData(): ShipTeleportData {
        return ShipTeleportDataImpl(
            position,
            orientation,
            velocity,
            omega,
            dimension,
            scale
        )
    }

    fun toWorldPosition(transform: ShipTransform) {
        val quat = transform.shipToWorldRotation
        orientation = Quaterniond(quat.x(), quat.y(), quat.z(), quat.w()).mul(
            orientation
        )
        position = transformPosition(transform, position)
    }


    private fun transformPosition(ship: ShipTransform, pos: Vector3d): Vector3d {
        return ship.shipToWorld.transformPosition(pos)
    }
}