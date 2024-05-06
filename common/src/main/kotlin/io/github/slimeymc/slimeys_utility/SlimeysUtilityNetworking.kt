package io.github.slimeymc.slimeys_utility

import dev.architectury.networking.NetworkManager
import io.github.slimeymc.slimeys_utility.client.gui.screens.ShipTaggerScreen
import io.github.slimeymc.slimeys_utility.physicify.scaleShip
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.util.readVec3d
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.vsCore

object SlimeysUtilityNetworking {
    val SHIP_SCALING_SCREEN_PACKET_ID = ResourceLocation(SlimeysUtilityMod.MOD_ID, "ship_scaling_screen")
    val SHIP_TAGGING_SCREEN_PACKET_ID = ResourceLocation(SlimeysUtilityMod.MOD_ID, "ship_tagging_screen")

    val SHIP_TAGGING_CONFIRM_PACKET_ID = ResourceLocation(SlimeysUtilityMod.MOD_ID, "ship_tagging_screen")

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
    }

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