package io.github.slimeymc.slimeys_utility.items

import dev.architectury.networking.NetworkManager
import io.github.slimeymc.slimeys_utility.SlimeysUtilityMod
import io.github.slimeymc.slimeys_utility.SlimeysUtilityNetworking.SHIP_TAGGING_SCREEN_PACKET_ID
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.util.writeVec3d
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.vsCore
import org.valkyrienskies.mod.util.logger

class ShipTaggerItem(properties: Properties) : ShipSelectingItem(properties) {
    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack> {
        _root_ide_package_.io.github.slimeymc.slimeys_utility.SlimeysUtilityMod.LOGGER.info("${player.name} hit ${selectedShip?.transform?.positionInWorld}")
        player.playSound(SoundType.AMETHYST_CLUSTER.placeSound, 1F, 1F)

        if (selectedShip != null) {
            _root_ide_package_.io.github.slimeymc.slimeys_utility.SlimeysUtilityMod.LOGGER.info("sending packet")
            val buf = FriendlyByteBuf(Unpooled.buffer())
            buf.writeVec3d(selectedShip!!.transform.positionInWorld)

            NetworkManager.sendToPlayer(player as ServerPlayer, SHIP_TAGGING_SCREEN_PACKET_ID, buf)
        }

        return super.use(level, player, interactionHand)
    }
}