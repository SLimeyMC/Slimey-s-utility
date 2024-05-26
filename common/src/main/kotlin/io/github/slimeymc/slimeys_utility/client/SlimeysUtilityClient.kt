package io.github.slimeymc.slimeys_utility.client

import io.github.slimeymc.slimeys_utility.SlimeysUtilityEntities
import io.github.slimeymc.slimeys_utility.SlimeysUtilityNetworkings
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment


@Environment(EnvType.CLIENT)
object SlimeysUtilityClient {
    @JvmStatic
    fun init() {
        SlimeysUtilityNetworkings.registerClient()
        SlimeysUtilityEntities.registerClient()
    }
}