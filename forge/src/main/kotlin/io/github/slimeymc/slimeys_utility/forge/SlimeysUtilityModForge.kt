package io.github.slimeymc.slimeys_utility.forge

import dev.architectury.platform.forge.EventBuses
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import io.github.slimeymc.slimeys_utility.SlimeysUtilityMod
import io.github.slimeymc.slimeys_utility.client.SlimeysUtilityClient
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(SlimeysUtilityMod.ID)
class SlimeysUtilityModForge {
    init {
        MOD_BUS.addListener { event: FMLClientSetupEvent? ->
            clientSetup(event)
        }

        EventBuses.registerModEventBus(SlimeysUtilityMod.ID, MOD_BUS)

        SlimeysUtilityMod.init()
    }

    private fun clientSetup(event: FMLClientSetupEvent?) {
        SlimeysUtilityClient.init()
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
