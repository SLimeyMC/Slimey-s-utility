package io.github.slimeymc.slimeys_utility.fabric;

import io.github.slimeymc.slimeys_utility.client.SlimeysUtilityClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SlimeysUtilityClientModFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SlimeysUtilityClient.init();
    }
}