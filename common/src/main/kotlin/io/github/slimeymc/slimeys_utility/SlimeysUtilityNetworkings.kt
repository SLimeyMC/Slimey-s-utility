package io.github.slimeymc.slimeys_utility

import dev.architectury.networking.NetworkManager
import io.github.slimeymc.slimeys_utility.client.gui.screens.ShipTaggerScreen
import io.github.slimeymc.slimeys_utility.items.ShipSelectionItem
import io.github.slimeymc.slimeys_utility.util.readAABBd
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.util.readVec3d
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.vsCore

object SlimeysUtilityNetworkings {
    val SHIP_SELECTION_ENTITY_PACKET_ID = ResourceLocation(SlimeysUtilityMod.ID, "ship_selection_entity")

    val SHIP_SCALING_SCREEN_PACKET_ID = ResourceLocation(SlimeysUtilityMod.ID, "ship_scaling_screen")
    val SHIP_TAGGING_SCREEN_PACKET_ID = ResourceLocation(SlimeysUtilityMod.ID, "ship_tagging_screen")

    val SHIP_TAGGING_CONFIRM_PACKET_ID = ResourceLocation(SlimeysUtilityMod.ID, "ship_tagging_confirm")

    fun registerServer() {
        NetworkManager.registerReceiver(
            NetworkManager.clientToServer(),
            SHIP_TAGGING_CONFIRM_PACKET_ID
        ) { buf, ctx ->
            SlimeysUtilityMod.LOGGER.info("procced with the request")
            val player = ctx.player // Add player to list of ship ownership
            val level = player.level
            val ship = level.getShipManagingPos(buf.readBlockPos()) ?: return@registerReceiver
            val name = buf.readUtf()
            ctx.queue {
                vsCore.renameShip(ship as ServerShip, name)
            }
        }
        NetworkManager.registerReceiver(
            NetworkManager.clientToServer(),
            SHIP_SELECTION_ENTITY_PACKET_ID
        ) { buf, ctx ->
            SlimeysUtilityMod.LOGGER.info("procced with the request")
            val player = ctx.player // Add player to list of ship ownership
            val level = player.level
            val ship = level.getShipManagingPos(buf.readBlockPos()) ?: return@registerReceiver
            val shipSelectionEntity = (player.mainHandItem.item as ShipSelectionItem).s
            val shipAABB = buf.readAABBd()
            ctx.queue {
                (player.mainHandItem.item as ShipSelectionItem).selectedShip
            }
        }
    }

    @Environment(EnvType.CLIENT)
    fun registerClient() {
        NetworkManager.registerReceiver(
            NetworkManager.serverToClient(),
            SHIP_TAGGING_SCREEN_PACKET_ID
        ) { buf, ctx ->
            SlimeysUtilityMod.LOGGER.info("receiving packet")
            val player = ctx.player
            val ship = player.level.getShipManagingPos(buf.readVec3d()) ?: return@registerReceiver
            SlimeysUtilityMod.LOGGER.info("ship found")
            ctx.queue {
                SlimeysUtilityMod.LOGGER.info("setting screen")
                Minecraft.getInstance()
                    .setScreen(ShipTaggerScreen(TranslatableComponent("slimeys_utility.ship_tagger.title"), ship))
            }
        }
    }
}