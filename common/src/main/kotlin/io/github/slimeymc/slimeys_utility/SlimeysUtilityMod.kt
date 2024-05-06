package io.github.slimeymc.slimeys_utility

import org.slf4j.LoggerFactory

object SlimeysUtilityMod {

    const val RENDER_TYPE = "slimeys_utility_render"

    const val MOD_ID = "slimeys_utility"

    val LOGGER = LoggerFactory.getLogger(_root_ide_package_.io.github.slimeymc.slimeys_utility.SlimeysUtilityMod.MOD_ID)

    @JvmStatic
    fun init() {
        _root_ide_package_.io.github.slimeymc.slimeys_utility.SlimeysUtilityItems.register()
        _root_ide_package_.io.github.slimeymc.slimeys_utility.SlimeysUtilityNetworking.registerServer()
    }
}
