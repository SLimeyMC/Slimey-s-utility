package io.github.priestoffern.vs_ship_assembler.mixin;

import io.github.priestoffern.vs_ship_assembler.util.PhysicUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
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

@Mixin(ShipAssemblyKt.class)
public final class ShipAssemblyMixin {

    // Overwrite the createNewShipWithBlocks function with our own function which wont break redstone stuff and cause duplication glitch
    @Inject(method = "createNewShipWithBlocks", at = @At("HEAD"), cancellable = true)
    private static void onCreateNewShipWithBlocks(@NotNull BlockPos centerBlock, @NotNull DenseBlockPosSet blocks, @NotNull ServerLevel level, CallbackInfoReturnable<ServerShip> cir) {
        Collection<BlockPos > blockList = null;
        blocks.forEach(vec -> {
            if (!level.getBlockState(new BlockPos(vec.x(), vec.y(), vec.z())).getTags().anyMatch(tag -> tag.equals(VsShipAssemblerTags.FORBIDDEN_ASSEMBLE))) {
                blockList.add(new BlockPos(vec.x(), vec.y(), vec.z()));
            }
        });
        cir.setReturnValue((ServerShip) PhysicUtility.INSTANCE.assembleToContraption((Level) level, (List<BlockPos>) blockList, true, 1.0));
    }
}
