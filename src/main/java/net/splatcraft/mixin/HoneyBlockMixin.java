package net.splatcraft.mixin;

import net.minecraft.block.HoneyBlock;
import net.minecraft.entity.Entity;
import net.splatcraft.entity.InkProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneyBlock.class)
public class HoneyBlockMixin {
    @Inject(method = "hasHoneyBlockEffects", at = @At("HEAD"), cancellable = true)
    private static void onHasHoneyBlockEffects(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof InkProjectileEntity) cir.setReturnValue(true);
    }
}
