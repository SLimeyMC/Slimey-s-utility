package io.github.slimeymc.slimeys_utility.items

import dev.architectury.networking.NetworkManager
import io.github.slimeymc.slimeys_utility.SlimeysUtilityMod.LOGGER
import io.github.slimeymc.slimeys_utility.SlimeysUtilityNetworkings.SHIP_SCALING_SCREEN_PACKET_ID
import io.github.slimeymc.slimeys_utility.physicify.scaleShip
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.util.writeVec3d
import org.valkyrienskies.mod.common.world.clipIncludeShips

class ShipScalerItem(properties: Properties) : ShipSelectionItem(properties) {
    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val clipResult = level.clipIncludeShips(clipFromPlayer(level, player, ClipContext.Fluid.NONE))
        LOGGER.info("${player.name} hit ${clipResult.blockPos}")
        player.playSound(SoundType.AMETHYST_CLUSTER.placeSound, 1F, 1F)

        if (selectedShip != null) {
            val buf = FriendlyByteBuf(Unpooled.buffer())
            buf.writeVec3d(selectedShip!!.transform.positionInWorld)

            NetworkManager.sendToPlayer(player as ServerPlayer, SHIP_SCALING_SCREEN_PACKET_ID, buf)
        }
        return super.use(level, player, interactionHand)
    }

    fun shipScaleConfirm(level: Level, player: Player, scale: Double) {
        var newScale = selectedShip!!.transform.shipToWorldScaling.x()
        scaleShip(level as ServerLevel, selectedShip as ServerShip, newScale)
    }
}