package io.github.priestoffern.vs_ship_assembler

import org.slf4j.LoggerFactory

object VsShipAssemblerMod {
    const val RENDER_TYPE = "vs_ship_assembly_render"

    const val MOD_ID = "vs_ship_assembler"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    @JvmStatic
    fun init() {
        VsShipAssemblerItems.register()
    }

    @JvmStatic
    fun initClient() {
    }
}
