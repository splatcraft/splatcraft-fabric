package net.splatcraft.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.entity.access.InkEntityAccess;
import net.splatcraft.entity.access.InkableCaster;
import net.splatcraft.entity.access.LivingEntityAccess;
import net.splatcraft.entity.damage.SplatcraftDamageSource;
import net.splatcraft.tag.SplatcraftEntityTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.splatcraft.particle.SplatcraftParticles.*;
import static net.splatcraft.world.SplatcraftGameRules.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements InkEntityAccess, LivingEntityAccess {
    @Shadow public abstract float getMaxHealth();
    @Shadow public abstract float getHealth();
    @Shadow public abstract void heal(float amount);

    @Unique
    private int ticksWithoutDamage;

    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void onTickMovement(CallbackInfo ci) {
        if (!this.world.isClient) {
            EntityType<?> type = this.getType();
            boolean isInSquidForm = this.isInSquidForm();

            // damage entity if on enemy ink
            if (this.isOnEnemyInk()) {
                this.resetTicksWithoutDamage(); // disallow increased health regeneration
                if (gameRule(this.world, DAMAGE_ON_ENEMY_INK) && SplatcraftEntityTypeTags.HURT_BY_ENEMY_INK.contains(type)) {
                    if (isInSquidForm || !gameRule(this.world, DAMAGE_ON_ENEMY_INK_ONLY_IN_SQUID_FORM)) {
                        boolean canKill = gameRule(this.world, DAMAGE_ON_ENEMY_INK_CAN_KILL);
                        float health = this.getHealth();
                        float max = this.getMaxHealthForOnEnemyInk();
                        if (canKill || health > max) {
                            float damage = 0.18f * this.getScaleForOnEnemyInk();
                            float amount = canKill ? damage : Math.min(health - max, damage); // cap damage at max health
                            this.damage(SplatcraftDamageSource.INKED_ENVIRONMENT, amount);
                        }
                    }
                }
            }

            // damage entity if is wet
            if (gameRule(this.world, DAMAGE_WHEN_WET) && SplatcraftEntityTypeTags.HURT_BY_WATER.contains(type)) {
                if (this.isWet() && (isInSquidForm || !gameRule(this.world, DAMAGE_WHEN_WET_ONLY_IN_SQUID_FORM))) {
                    boolean canKill = gameRule(this.world, DAMAGE_WHEN_WET_CAN_KILL);
                    if (gameRule(this.world, DAMAGE_WHEN_WET_INSTANT_KILL) && canKill) {
                        this.damage(SplatcraftDamageSource.INK_IN_WATER, Float.MAX_VALUE);
                    } else {
                        float maxHealth = this.getMaxHealth();
                        float scale = gameRule(this.world, DAMAGE_WHEN_WET_SCALES_TO_MAX_HEALTH) ? maxHealth : 20;
                        if (canKill || this.getHealth() > (maxHealth - (0.8f * scale))) {
                            this.damage(SplatcraftDamageSource.INK_IN_WATER, 0.2f * scale);
                        }
                    }
                }
            }

            // splatoon-accurate health regeneration
            this.ticksWithoutDamage++;
            if (this.canFastHeal()) {
                float maxHealth = this.getMaxHealth();
                if (gameRule(this.world, REGENERATE_WHEN_SUBMERGED) && this.isSubmergedInInk()) {
                    float scale = gameRule(this.world, REGENERATE_WHEN_SUBMERGED_SCALES_TO_MAX_HEALTH) ? maxHealth : 20;
                    this.heal((1.0f / 20) * scale);
                } else if (gameRule(this.world, GAME_HEALTH) && (isInSquidForm || !(gameRule(this.world, GAME_HEALTH_ONLY_IN_SQUID_FORM)))) {
                    float scale = gameRule(this.world, GAME_HEALTH_SCALES_TO_MAX_HEALTH) ? maxHealth : 20;
                    this.heal((0.125f / 20) * scale);
                }
            }
        }
    }

    @Unique
    @Override
    public float getScaleForOnEnemyInk() {
        return gameRule(this.world, DAMAGE_ON_ENEMY_INK_SCALES_TO_MAX_HEALTH) ? this.getMaxHealth() : 20;
    }

    @Unique
    @Override
    public float getMaxHealthForOnEnemyInk() {
        return this.getMaxHealth() - (0.4f * this.getScaleForOnEnemyInk());
    }

    @Unique
    @Override
    public boolean canFastHeal() {
        return this.ticksWithoutDamage > 20;
    }

    @Unique
    @Override
    public void resetTicksWithoutDamage() {
        this.ticksWithoutDamage = 0;
    }

    /**
     * Spawns an ink squid soul particle on death if inkable.
     */
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

    /**
     * Ensures that the player is invisible when submerged.
     */
    @Inject(method = "updatePotionVisibility", at = @At("HEAD"), cancellable = true)
    private void onUpdatePotionVisibility(CallbackInfo ci) {
        if (this.isSubmergedInInk()) {
            this.setInvisible(true);
            ci.cancel();
        }
    }

    /**
     * Disables fall damage when on own ink.
     */
    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void onHandleFallDamage(float fallDistance, float damageMultiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (this.isOnOwnInk() && this.isInSquidForm()) cir.setReturnValue(false);
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            this.resetTicksWithoutDamage();
            if (!gameRule(this.world, GAME_HEALTH_INVULNERABILITY_TICKS_ON_INK_DAMAGE) && source.name.equals(SplatcraftDamageSource.ID_INKED)) {
                this.timeUntilRegen = 10;
            }
        }
    }
}
