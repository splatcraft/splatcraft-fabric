package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.handler.WeaponHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        WeaponHandler.onLivingDamage(LivingEntity.class.cast(this), amount);
    }
}
