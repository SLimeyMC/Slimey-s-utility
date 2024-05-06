package io.github.slimeymc.slimeys_utility.client

import dev.architectury.networking.NetworkManager.*
import io.github.slimeymc.slimeys_utility.SlimeysUtilityMod
import io.github.slimeymc.slimeys_utility.SlimeysUtilityNetworking
import io.github.slimeymc.slimeys_utility.client.gui.screens.ShipTaggerScreen
import net.fabricmc.api.EnvType
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.valkyrienskies.mod.common.getShipManagingPos


object SlimeysUtilityClient {
    @JvmStatic
    fun init() {
        SlimeysUtilityNetworking.registerClient()
    }
}