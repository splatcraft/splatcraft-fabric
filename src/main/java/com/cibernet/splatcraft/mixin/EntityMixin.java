package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void playStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        Entity $this = Entity.class.cast(this);
        if ($this instanceof PlayerEntity) {
            if (LazyPlayerDataComponent.isSquid((PlayerEntity) $this)) {
                ci.cancel();
            }
        }
    }
}
