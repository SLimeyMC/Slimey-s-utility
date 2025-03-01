package io.github.slimeymc.slimeys_utility.fabric;

import io.github.slimeymc.slimeys_utility.SlimeysUtilityMod;
import io.github.slimeymc.slimeys_utility.client.SlimeysUtilityClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class SlimeysUtilityModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before eureka
        new ValkyrienSkiesModFabric().onInitialize();

        SlimeysUtilityMod.init();
    }
}
