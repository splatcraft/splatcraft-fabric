package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.component.SplatcraftComponents;
import com.cibernet.splatcraft.handler.PlayerHandler;
import com.cibernet.splatcraft.handler.WeaponHandler;
import com.cibernet.splatcraft.init.SplatcraftAttributes;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Shadow @Final public PlayerAbilities abilities;

    private Vec3d splatcraft_posLastTick = Vec3d.ZERO;

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
       cir.setReturnValue(cir.getReturnValue().add(SplatcraftAttributes.INK_SWIM_SPEED, SplatcraftAttributes.INK_SWIM_SPEED.getDefaultValue()));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        PlayerEntity player = PlayerEntity.class.cast(this);
        PlayerHandler.onPlayerTick(player);
        WeaponHandler.onPlayerTick(player);
    }

    @Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
    private void getMovementSpeed(CallbackInfoReturnable<Float> cir) {
        PlayerEntity $this = PlayerEntity.class.cast(this);
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get($this);

        if (data.isSquid() && !this.abilities.flying) {
            float movementSpeed = PlayerHandler.getMovementSpeed($this, cir.getReturnValueF());
            if (movementSpeed != -1.0f) {
                cir.setReturnValue(cir.getReturnValueF() * movementSpeed);
            }
        }
    }

    @Inject(method = "travel", at = @At("TAIL"))
    private void travel(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity $this = PlayerEntity.class.cast(this);
        if (!$this.world.isClient) {
            double threshold = 0.13d;
            if (InkBlockUtils.shouldBeSubmerged($this) && (Math.abs(splatcraft_posLastTick.getX() - $this.getX()) >= threshold || Math.abs(splatcraft_posLastTick.getY() - $this.getY()) >= threshold || Math.abs(splatcraft_posLastTick.getZ() - $this.getZ()) >= threshold)) {
                ColorUtils.playSquidTravelEffects($this, ColorUtils.getInkColor($this), 1.0f);
            }

            splatcraft_posLastTick = $this.getPos();
        }
    }

    @Inject(method = "damage", at = @At("TAIL"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity $this = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.getComponent($this);
        if (data.isSquid()) {
            for (int i = 0; i < Math.min(amount, 24); ++i) {
                ColorUtils.addInkSplashParticle($this.world, data.getInkColor(), new Vec3d($this.getParticleX(0.5d), $this.getRandomBodyY() - 0.25d, $this.getParticleZ(0.5d)), 0.4f);
            }
        }
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(PlayerEntity.class.cast(this));
        if (data.isSquid()) {
            cir.setReturnValue(data.isSubmerged() ? PlayerHandler.SQUID_FORM_DIMENSIONS : PlayerHandler.SQUID_FORM_AIRBORNE_DIMENSIONS);
        }
    }
    @Inject(method = "getActiveEyeHeight", at = @At("RETURN"), cancellable = true)
    private void getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        PlayerEntity $this = PlayerEntity.class.cast(this);
        try {
            PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get($this);
            if (data.isSquid()) {
                cir.setReturnValue(dimensions.height / (
                        data.isSubmerged()
                            ? 3.0f
                            : $this.isOnGround()
                                ? 2.6f
                                : 1.2f
                    )
                );
            }
        } catch (NullPointerException ignored) {}
    }

    @Inject(method = "isBlockBreakingRestricted", at = @At("HEAD"), cancellable = true)
    private void isBlockBreakingRestricted(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
        if (PlayerHandler.shouldCancelInteraction(PlayerEntity.class.cast(this))) {
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void attack(Entity target, CallbackInfo ci) {
        if (PlayerHandler.shouldCancelInteraction(PlayerEntity.class.cast(this))) {
            ci.cancel();
        }
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        PlayerEntity $this = PlayerEntity.class.cast(this);
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get($this);
        if (data.isSquid()) {
            cir.setReturnValue(SoundEvents.ENTITY_COD_DEATH);
        }
    }
    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void getHurtSound(CallbackInfoReturnable<SoundEvent> cir) {
        PlayerEntity $this = PlayerEntity.class.cast(this);
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get($this);
        if (data.isSquid()) {
            cir.setReturnValue(SoundEvents.ENTITY_COD_HURT);
        }
    }
}
