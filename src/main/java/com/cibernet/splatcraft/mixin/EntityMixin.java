package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void playStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        PlayerEntity $this = PlayerEntity.class.cast(this);
        if ($this != null) {
            PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get($this);
            if (data.isSquid()) {
                if ($this.getRandom().nextFloat() <= 0.685F) {
                    this.playSound(SoundEvents.BLOCK_HONEY_BLOCK_FALL, 0.15F, 1.0F);
                }
                ci.cancel();
            }
        }
    }
}
