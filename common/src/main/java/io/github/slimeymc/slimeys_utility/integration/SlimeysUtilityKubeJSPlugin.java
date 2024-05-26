package io.github.slimeymc.slimeys_utility.integration;

import dev.latvian.mods.kubejs.script.BindingsEvent;
import io.github.slimeymc.slimeys_utility.physicify.ShipPhysicifyKt;
import io.github.slimeymc.slimeys_utility.util.VectorConversionKt;


public class SlimeysUtilityKubeJSPlugin {
    public static void addBindings(BindingsEvent event) {
        // Vector Conversion
        event.add("VectorConversionsExt", VectorConversionKt.class);

        // ShipPhysicify
        event.add("ShipPhysicify", ShipPhysicifyKt.class);
    }
}