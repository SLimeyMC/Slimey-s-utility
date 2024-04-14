package io.github.priestoffern.vs_ship_assembler.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import io.github.priestoffern.vs_ship_assembler.VsShipAssemblerTags;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;

import java.util.Collection;
import java.util.List;

import static io.github.priestoffern.vs_ship_assembler.util.PhysicsUtilityKt.assembleToContraption;

@Mixin(ShipAssemblyKt.class)
public final class ShipAssemblyMixin {

    // Overwrite the createNewShipWithBlocks function with our own function which won't break redstone stuff
    // and create duplication glitch on create stuff
    @Inject(method = "createNewShipWithBlocks", at = @At("HEAD"), cancellable = true)
    private static void onCreateNewShipWithBlocks(@NotNull BlockPos centerBlock, @NotNull DenseBlockPosSet blocks, @NotNull ServerLevel level, CallbackInfoReturnable<ServerShip> cir) {
        List<BlockPos> blockList = List.of(blocks.stream()
                .filter(vec -> level.getBlockState(new BlockPos(vec.x(), vec.y(), vec.z()))
                        .getTags().noneMatch(tag -> tag.equals(VsShipAssemblerTags.FORBIDDEN_ASSEMBLE)))
                .map(vec -> new BlockPos(vec.x(), vec.y(), vec.z()))
                .toArray(BlockPos[]::new));
        cir.setReturnValue((ServerShip) assembleToContraption(level, blockList, true, 1.0));
    }
}
