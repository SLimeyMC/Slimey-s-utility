package io.github.priestoffern.vs_ship_assembler.items

import io.github.priestoffern.vs_ship_assembler.physicify.scaleShip
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.vsCore
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.mod.util.logger

class ShipScalerItem(properties: Properties) : ShipSelectingItem(properties) {
    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val clipResult = level.clipIncludeShips(clipFromPlayer(level, player, ClipContext.Fluid.NONE))
        logger("${player.name} hit ${clipResult.blockPos}")
        player.playSound(SoundType.AMETHYST_CLUSTER.placeSound, 1F, 1F)

        var ship = level.getShipManagingPos(clipResult.blockPos)
        if (ship != null) {
            var newScale = 1.0
            scaleShip(level as ServerLevel, ship as ServerShip, newScale)
        }

        return super.use(level, player, interactionHand)
    }
}