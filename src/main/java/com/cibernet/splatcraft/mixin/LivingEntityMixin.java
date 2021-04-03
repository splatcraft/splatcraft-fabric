package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.handler.WeaponHandler;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        WeaponHandler.onLivingDamage(LivingEntity.class.cast(this), amount);
    }

    //
    // Amend momentum when a player jumps in squid form.
    //

    private InkColor splatcraft_lastLandedBlockInkColor = InkColors.NONE;

    @SuppressWarnings("ConstantConditions")
    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.91f, ordinal = 0)) // targets the FIRST place a float is defined as 0.91f, on ground
    private float checkLastLandedBlock(float c) {
        LivingEntity $this = LivingEntity.class.cast(this);
        if ($this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) $this;
            if (LazyPlayerDataComponent.isSquid(player)) {
                InkColor inkColor = ColorUtils.getInkColor(player.world.getBlockEntity(player.getVelocityAffectingPos()));
                if (inkColor.equals(InkColors.NONE) && player.world.getBlockState(player.getVelocityAffectingPos()).isAir()) {
                    return c;
                }

                splatcraft_lastLandedBlockInkColor = inkColor;
            }
        }

        return c;
    }
    @SuppressWarnings("ConstantConditions")
    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.91f, ordinal = 1)) // targets the SECOND place a float is defined as 0.91f, not on ground
    private float fixSquidAirborneVelocity(float c) {
        LivingEntity $this = LivingEntity.class.cast(this);
        if ($this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) $this;

            if (player.abilities.flying) {
                splatcraft_lastLandedBlockInkColor = InkColors.NONE;
            }

            if (LazyPlayerDataComponent.isSquid(player) && !splatcraft_lastLandedBlockInkColor.equals(InkColors.NONE) && (SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.UNIVERSAL_INK) || (!splatcraft_lastLandedBlockInkColor.equals(
                InkColors.NONE) && splatcraft_lastLandedBlockInkColor.matches(ColorUtils.getInkColor(player).color)))) {
                ColorUtils.playSquidTravelEffects($this, splatcraft_lastLandedBlockInkColor, 0.335f);
                return 0.9914f;
            }
        }

        return c; // default to original, pre-mixin value
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "getJumpVelocity", at = @At("RETURN"), cancellable = true)
    private void modifySquidJumpVelocity(CallbackInfoReturnable<Float> cir) {
        LivingEntity $this = LivingEntity.class.cast(this);
        if ($this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) $this;
            if (LazyPlayerDataComponent.isSquid(player) && InkBlockUtils.canSwim(player)) {
                cir.setReturnValue(cir.getReturnValueF() * 1.208f);
            }
        }
    }
}
