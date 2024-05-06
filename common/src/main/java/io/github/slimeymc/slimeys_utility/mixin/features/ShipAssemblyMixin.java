package io.github.slimeymc.slimeys_utility.mixin.features;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;

import static io.github.slimeymc.slimeys_utility.physicify.ShipPhysicifyKt.physicifyBlocks;

@Mixin(ShipAssemblyKt.class)
public final class ShipAssemblyMixin {

    // Overwrite the createNewShipWithBlocks function with our own function which won't break redstone stuff
    // and create duplication glitch on create stuff. it also block illegal block from physicified
    @Inject(method = "createNewShipWithBlocks", at = @At("HEAD"), cancellable = true)
    private static void onCreateNewShipWithBlocks(@NotNull BlockPos centerBlock, @NotNull DenseBlockPosSet blocks, @NotNull ServerLevel level, CallbackInfoReturnable<ServerShip> cir) {
        // TODO configurable forbidden assemble tag and a boolean to check if none match it here
        cir.setReturnValue(physicifyBlocks(level, blocks, centerBlock, 1.0).getFirst());
    }
}
