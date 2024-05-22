package io.github.slimeymc.slimeys_utility.client

import io.github.slimeymc.slimeys_utility.SlimeysUtilityNetworkings


object SlimeysUtilityClient {
    @JvmStatic
    fun init() {
        SlimeysUtilityNetworkings.registerClient()
    }
}