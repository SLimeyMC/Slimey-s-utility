package io.github.slimeyar.slimeys_utility.fabric;

import io.github.slimeyar.slimeys_utility.SlimeysUtilityMod;
import io.github.slimeyar.slimeys_utility.client.SlimeysUtilityClient;
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

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            SlimeysUtilityClient.init();
        }
    }
}
