package net.splatcraft.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.entity.InkableCaster;
import net.splatcraft.entity.damage.SplatcraftDamageSource;
import net.splatcraft.tag.SplatcraftEntityTypeTags;
import net.splatcraft.world.SplatcraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.splatcraft.particle.SplatcraftParticles.inkSquidSoul;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // damage entity if on enemy ink and is squid
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void onTickMovement(CallbackInfo ci) {
        LivingEntity that = LivingEntity.class.cast(this);
        if (!this.world.isClient) {
            if (this.world.getGameRules().getBoolean(SplatcraftGameRules.HURT_INK_SQUIDS_ON_ENEMY_INK) && ((InkEntityAccess) this).isOnEnemyInk()) {
                if (that instanceof PlayerEntity player) {
                    PlayerDataComponent data = PlayerDataComponent.get(player);
                    if (data.isSquid()) this.damage(SplatcraftDamageSource.INKED_ENVIRONMENT, 1.0f);
                } else if (SplatcraftEntityTypeTags.HURT_BY_ENEMY_INK.contains(this.getType())) this.damage(SplatcraftDamageSource.INKED_ENVIRONMENT, 1.0f);
            }
        }
    }

    // damage entity if in water or is player and is squid
    @Inject(method = "hurtByWater", at = @At("HEAD"), cancellable = true)
    private void onHurtByWater(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity that = LivingEntity.class.cast(this);
        if (this.world.getGameRules().getBoolean(SplatcraftGameRules.HURT_INK_SQUIDS_IN_WATER)) {
            if (that instanceof PlayerEntity player) {
                PlayerDataComponent data = PlayerDataComponent.get(player);
                if (data.isSquid()) cir.setReturnValue(true);
            } else if (SplatcraftEntityTypeTags.HURT_BY_WATER.contains(this.getType())) cir.setReturnValue(true);
        }
    }

    // spawn ink squid soul particle on death if inkable
    @Environment(EnvType.CLIENT)
    @Inject(method = "handleStatus", at = @At("HEAD"))
    private void onHandleStatus(byte status, CallbackInfo ci) {
        if (ClientConfig.INSTANCE.inkSquidSoulParticleOnDeath.getValue()) {
            if (status == 3 && this instanceof InkableCaster caster) {
                EntityDimensions dimensions = this.getDimensions(this.getPose());
                Vec3d pos = this.getPos().add(0.0d, dimensions.height + 0.5f, 0.0d);
                inkSquidSoul(caster.toInkable(), pos, 1.0f);
            }
        }
    }

    // ensure that the player is invisible when submerged
    @Inject(method = "updatePotionVisibility", at = @At("HEAD"), cancellable = true)
    private void onUpdatePotionVisibility(CallbackInfo ci) {
        LivingEntity that = LivingEntity.class.cast(this);
        if (that instanceof PlayerEntity player) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            if (data.isSubmerged()) {
                this.setInvisible(true);
                ci.cancel();
            }
        }
    }

    // prevent fall damage when on own ink
    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void onHandleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity that = LivingEntity.class.cast(this);
        if (((InkEntityAccess) this).isOnOwnInk()) {
            if (that instanceof PlayerEntity player) {
                PlayerDataComponent data = PlayerDataComponent.get(player);
                if (data.isSubmerged()) cir.setReturnValue(false);
            } else cir.setReturnValue(false);
        }
    }
}
