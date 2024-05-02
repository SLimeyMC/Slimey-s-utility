package io.github.slimeyar.slimeys_utility

import dev.architectury.networking.NetworkManager
import dev.architectury.networking.simple.MessageDecoder
import dev.architectury.networking.simple.MessageType
import dev.architectury.networking.simple.SimpleNetworkManager
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import org.slf4j.LoggerFactory

object SlimeysUtilityMod {

    const val RENDER_TYPE = "slimeys_utility_render"

    const val MOD_ID = "slimeys_utility"

    val SHIP_SCALING_SCREEN_PACKET_ID = ResourceLocation(MOD_ID, "ship_scaling_screen")
    val SHIP_TAGGING_SCREEN_PACKET_ID = ResourceLocation(MOD_ID, "ship_tagging_screen")

    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    @JvmStatic
    fun init() {
        SlimeysUtilityItems.register()
    }
}
