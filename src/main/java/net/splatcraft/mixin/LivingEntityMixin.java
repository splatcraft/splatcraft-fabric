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
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.entity.InkableCaster;
import net.splatcraft.entity.damage.SplatcraftDamageSource;
import net.splatcraft.tag.SplatcraftEntityTypeTags;
import net.splatcraft.world.SplatcraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.splatcraft.particle.SplatcraftParticles.inkSquidSoul;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract void heal(float amount);

    @Shadow public abstract float getHealth();

    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // damage entity if on enemy ink and is squid
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void onTickMovement(CallbackInfo ci) {
        LivingEntity that = LivingEntity.class.cast(this);
        if (!this.world.isClient) {
            GameRules rules = this.world.getGameRules();
            PlayerDataComponent data = null;
            boolean isPlayer = that instanceof PlayerEntity;
            if (isPlayer) {
                PlayerEntity player = (PlayerEntity) that;
                data = PlayerDataComponent.get(player);
            }
            if (((InkEntityAccess) this).isOnEnemyInk()) {
                if (rules.getBoolean(SplatcraftGameRules.HURT_INK_SQUIDS_ON_ENEMY_INK)) {
                    float damage = rules.getBoolean(SplatcraftGameRules.ENEMY_INK_DAMAGE_SCALES_TO_MAX_HEALTH) ? that.getMaxHealth() * 0.09f : 1.8f;
                    if (isPlayer) {
                        float maxDamage = rules.getBoolean(SplatcraftGameRules.ENEMY_INK_DAMAGE_SCALES_TO_MAX_HEALTH) ? that.getMaxHealth() * 0.4f : 8;
                        if (data.getDamageTakenOnEnemyInk() < maxDamage && (data.isSquid() || !rules.getBoolean(SplatcraftGameRules.ENEMY_INK_DAMAGE_ONLY_IN_SQUID_FORM))) {
                            this.damage(SplatcraftDamageSource.INKED_ENVIRONMENT, damage);
                            data.addDamageTakenOnEnemyInk(damage);
                            data.resetTicksWithoutDamage();
                        }
                    } else if (SplatcraftEntityTypeTags.HURT_BY_ENEMY_INK.contains(this.getType()))
                        this.damage(SplatcraftDamageSource.INKED_ENVIRONMENT, damage);
                }
            } else if (isPlayer)
                data.addTicksWithoutDamage(1);
            if (rules.getBoolean(SplatcraftGameRules.HURT_INK_SQUIDS_IN_WATER) && that.isWet()) {
                float damageScaled = rules.getBoolean(SplatcraftGameRules.WATER_DAMAGE_SCALES_TO_MAX_HEALTH) ? that.getMaxHealth() / 5 : 4;
                float damage = rules.getBoolean(SplatcraftGameRules.WATER_DAMAGE_KILLS_INSTANTLY) ? Float.MAX_VALUE : damageScaled;
                if (isPlayer) {
                    if (data.isSquid() || !rules.getBoolean(SplatcraftGameRules.WATER_DAMAGE_ONLY_IN_SQUID_FORM)) {
                        this.damage(SplatcraftDamageSource.WATER, damage);
                        data.resetTicksWithoutDamage();
                    }
                } else if (SplatcraftEntityTypeTags.HURT_BY_WATER.contains(this.getType()))
                    this.damage(SplatcraftDamageSource.WATER, damage);
            }

            if (isPlayer && rules.getBoolean(SplatcraftGameRules.SPLATOON_HEALTH_REGENERATION)) {
                if (data.getPrevHealth() > this.getHealth())
                    data.resetTicksWithoutDamage();
                if (this.getHealth() <= 0)
                    data.resetDamageTakenOnEnemyInk();
                if (data.getTicksWithoutDamage() >= 20) {
                    float healing = 0;
                    boolean scalesToMax = rules.getBoolean(SplatcraftGameRules.HEALTH_REGENERATION_SCALES_TO_MAX_HEALTH);
                    if (data.isSubmerged())
                        healing = scalesToMax ? that.getMaxHealth() / 20 : 1;
                    else if (!rules.getBoolean(SplatcraftGameRules.REGENERATE_HEALTH_ONLY_IN_SQUID_FORM))
                        healing = scalesToMax ? that.getMaxHealth() * 0.00625f : 0.125f;
                    data.addDamageTakenOnEnemyInk(-healing);
                    this.heal(healing);
                    if (this.getHealth() > data.getPrevHealth())
                        data.addDamageTakenOnEnemyInk(data.getPrevHealth() - this.getHealth());
                }
                data.setPrevHealth(this.getHealth());
            }
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
                if (data.isSquid()) cir.setReturnValue(false);
            } else cir.setReturnValue(false);
        }
    }
}
