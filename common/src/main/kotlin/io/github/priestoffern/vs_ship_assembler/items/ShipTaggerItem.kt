package io.github.priestoffern.vs_ship_assembler.items

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.vsCore
import org.valkyrienskies.mod.util.logger

class ShipTaggerItem(properties: Properties) : ShipSelectingItem(properties) {
    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack> {
        logger("${player.name} hit ${selectedShip?.transform?.positionInWorld}")
        player.playSound(SoundType.AMETHYST_CLUSTER.placeSound, 1F, 1F)

        if (selectedShip != null) {
            var newName = ""
            vsCore.renameShip(selectedShip as ServerShip, newName)
        }

        return super.use(level, player, interactionHand)
    }
}