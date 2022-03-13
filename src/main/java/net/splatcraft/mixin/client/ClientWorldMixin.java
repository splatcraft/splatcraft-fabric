package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.splatcraft.api.block.SplatcraftBlocks;
import net.splatcraft.impl.client.config.ClientConfig;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Shadow public abstract void randomBlockDisplayTick(int centerX, int centerY, int centerZ, int radius, Random random, @Nullable Block block, BlockPos.Mutable pos);

    /**
     * Displays stage blocks if the client player is nearby.
     */
    @Inject(method = "doRandomBlockDisplayTicks", at = @At("TAIL"))
    private void ondoRandomBlockDisplayTicks(int centerX, int centerY, int centerZ, CallbackInfo ci) {
        Random random = new Random();
        int radius = ClientConfig.INSTANCE.stageBlockRadius.getValue();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < 5; i++) {
            for (Block block : new Block[]{ SplatcraftBlocks.STAGE_BARRIER, SplatcraftBlocks.STAGE_VOID }) {
                this.randomBlockDisplayTick(centerX, centerY, centerZ, radius, random, block, mutable);
            }
        }
    }
}
