package io.github.priestoffern.vs_ship_assembler.integration;

import dev.latvian.mods.kubejs.script.BindingsEvent;
import io.github.priestoffern.vs_ship_assembler.physicify.ShipPhysicifyKt;
import io.github.priestoffern.vs_ship_assembler.util.VectorConversionKt;


public class VsShipAssemblerKubeJSPlugin {
    public static void addBindings(BindingsEvent event) {
        // Vector Conversion
        event.add("VectorConversionsExt", VectorConversionKt.class);

        // ShipPhysicify
        event.add("ShipPhysicify", ShipPhysicifyKt.class);
    }
}