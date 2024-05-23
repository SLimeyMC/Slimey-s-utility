package io.github.slimeymc.slimeys_utility

import org.slf4j.LoggerFactory

object SlimeysUtilityMod {

    const val RENDER_TYPE = "slimeys_utility_render"

    const val ID = "slimeys_utility"

    val LOGGER = LoggerFactory.getLogger(ID)

    @JvmStatic
    fun init() {
        SlimeysUtilityItems.register()
        SlimeysUtilityNetworkings.registerServer()
        SlimeysUtilityEntities.registerServer()
    }
}
