package io.github.priestoffern.vs_ship_assembler

import java.util.logging.Logger

object VsShipAssemblerMod {
    const val MOD_ID = "vs_ship_assembler"
    val LOGGER = Logger.getLogger("VsShipAssembler")

    @JvmStatic
    fun init() {
        VsShipAssemblerItems.register()
    }

    @JvmStatic
    fun initClient() {
    }
}
