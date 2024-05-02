package io.github.slimeyar.slimeys_utility.client

import dev.architectury.networking.NetworkManager.*
import io.github.slimeyar.slimeys_utility.SlimeysUtilityMod
import io.github.slimeyar.slimeys_utility.client.gui.screens.ShipTaggerScreen
import net.fabricmc.api.EnvType
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.valkyrienskies.mod.common.getShipManagingPos


object SlimeysUtilityClient {
    @JvmStatic
    fun init() {
        registerReceiver(s2c(), SlimeysUtilityMod.SHIP_TAGGING_SCREEN_PACKET_ID) { buf, ctx ->
            val player = ctx.player
            val ship = player.level.getShipManagingPos(buf.readBlockPos()) ?: return@registerReceiver
            ctx.queue {
                Minecraft.getInstance()
                        .setScreen(ShipTaggerScreen(Component.nullToEmpty("Ship Tagger"), player, ship))
            }
        }
    }
}