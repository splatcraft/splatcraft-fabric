package net.splatcraft.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.*;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.util.SplatcraftConstants;
import net.splatcraft.world.SplatcraftGameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Optional;

import static net.splatcraft.util.Events.tickInkable;
import static net.splatcraft.particle.SplatcraftParticles.inkSplash;
import static net.splatcraft.util.SplatcraftConstants.SQUID_FORM_DIMENSIONS;
import static net.splatcraft.util.SplatcraftConstants.SQUID_FORM_SUBMERGED_DIMENSIONS;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Inkable, InkableCaster, PlayerEntityAccess {
    @Shadow @Final private PlayerAbilities abilities;

    @Shadow public abstract Text getDisplayName();
    @Shadow public abstract void increaseTravelMotionStats(double dx, double dy, double dz);
    @Shadow public abstract void stopFallFlying();

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public InkColor getInkColor() {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        return data.getInkColor();
    }

    @Override
    public boolean setInkColor(InkColor inkColor) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        return data.setInkColor(inkColor);
    }

    @Override
    public Text getTextForCommand() {
        return this.getDisplayName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Entity & Inkable> T toInkable() {
        return (T) this;
    }

    @Override
    public boolean updateSplatfestBand() {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);

        if (that.world.getGameRules().getBoolean(SplatcraftGameRules.SPLATFEST_BAND_MUST_BE_HELD)) {
            for (Hand hand : Hand.values()) {
                if (that.getStackInHand(hand).isOf(SplatcraftItems.SPLATFEST_BAND)) return data.setHasSplatfestBand(true);
            }
        } else if (that.getInventory().containsAny(Collections.singleton(SplatcraftItems.SPLATFEST_BAND))) return data.setHasSplatfestBand(true);

        return data.setHasSplatfestBand(false);
    }

    @Override
    public Optional<Float> getMovementSpeedM(float base) {
        if (((InkEntityAccess) this).canSubmergeInInk()) {
            return Optional.of(base * ((float) this.getAttributeValue(SplatcraftAttributes.INK_SWIM_SPEED) * 10));
        }

        return Optional.empty();
    }

    // cancel exhaustion if squid
    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    private void onAddExhaustion(float exhaustion, CallbackInfo ci) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        if (data.isSquid()) ci.cancel();
    }

    // change attributes for squid form
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(cir.getReturnValue()
                              .add(SplatcraftAttributes.INK_SWIM_SPEED)
                              .add(SplatcraftAttributes.INK_JUMP_FORCE)
        );
    }

    // modify movement speed for squid form
    @Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
    private void getMovementSpeed(CallbackInfoReturnable<Float> cir) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        if (!this.abilities.flying) {
            ((PlayerEntityAccess) that).getMovementSpeedM(cir.getReturnValueF())
                                       .ifPresent(cir::setReturnValue);
        }
    }

    // change pose for squid form
    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void onUpdatePose(CallbackInfo ci) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        if (data.isSquid() && !this.abilities.flying) {
            this.setPose(EntityPose.SWIMMING);
            ci.cancel();
        }
    }

    // replace dimensions for squid form
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void onGetDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        if (data.isSquid()) {
            cir.setReturnValue(data.isSubmerged() ? SQUID_FORM_SUBMERGED_DIMENSIONS : SQUID_FORM_DIMENSIONS);
        }
    }

    // replace eye height for squid form
    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        try {
            PlayerDataComponent data = PlayerDataComponent.get(that);
            if (data.isSquid()) cir.setReturnValue(SplatcraftConstants.getEyeHeight(data.isSubmerged()));
        } catch (NullPointerException ignored) {}
    }

    // add extra force to a submerged jump
    @Inject(method = "jump", at = @At("TAIL"))
    private void onJump(CallbackInfo ci) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        if (data.isSubmerged()) {
            Vec3d velocity = this.getVelocity();
            if (that.forwardSpeed > 0) {
                float f = this.getYaw() * ((float)Math.PI / 180);
                double m = this.getAttributeValue(SplatcraftAttributes.INK_JUMP_FORCE);
                this.setVelocity(
                    velocity.add(-MathHelper.sin(f) * m, 0.0d, MathHelper.cos(f) * m)
                            .multiply(1.0d, 0.875d, 1.0d)
                );
                this.velocityDirty = true;
            }
        }
    }

    private Vec3d posLastTick;
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickMovement(CallbackInfo ci) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        InkEntityAccess access = ((InkEntityAccess) that);
        PlayerDataComponent data = PlayerDataComponent.get(that);

        // check for submersion
        if (data.setSubmerged(access.canSubmergeInInk())) inkSplash(this, access.getInkSplashParticlePos(), 1.0f);

        // tick movement
        Vec3d pos = this.getPos();
        if (this.posLastTick != null) tickInkable(this, this.posLastTick.subtract(pos));
        this.posLastTick = pos;
    }

    // disable flying in squid form
    @Inject(method = "checkFallFlying", at = @At("HEAD"), cancellable = true)
    private void onCheckFallFlying(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        if (data.isSquid()) {
            this.stopFallFlying();
            cir.setReturnValue(false);
        }
    }

    // override travel with custom logic if climbing ink
    @SuppressWarnings("deprecation")
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        if (data.isSquid() && ((InkEntityAccess) that).canClimbInk()) {
            ci.cancel();

            double dx = this.getX();
            double dy = this.getY();
            double dz = this.getZ();

            InputPlayerEntityAccess inputAccess = ((InputPlayerEntityAccess) this);

            double gravity = 0.08d;
            BlockPos vpos = this.getVelocityAffectingPos();

            // if slow falling, reduce gravity
            boolean falling = this.getVelocity().y <= 0.0d;
            if (falling && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                gravity = 0.01d;
                this.onLanding();
            }

            // process input
            float slipperiness = this.world.getBlockState(vpos).getBlock().getSlipperiness();
            Vec3d applied = this.applyMovementInput(movementInput, slipperiness);

            // convert forwards input to upwards
            double upward = (inputAccess.isForwardPressed() || inputAccess.isSidewaysPressed() ? 0.3d : 0.0d);
            double y = Math.min(applied.y + upward, that.getAttributeValue(SplatcraftAttributes.INK_SWIM_SPEED) * 0.75d);

            // apply gravity
            if (this.world.isChunkLoaded(vpos)) {
                if (!this.hasNoGravity()) y -= gravity;
            } else {
                y = this.getY() > (double)this.world.getBottomY() ? -0.1d : 0.0d;
            }

            // speed up/slow down speed conditionally
            if (this.isSneaking()) y /= 1.5d;
            if (this.jumping) y = Math.abs(y * 1.25d);

            // set velocity
            double friction = this.onGround ? slipperiness * 0.91d : 0.91d;
            this.setVelocity(applied.x * friction, y * 0.98d, applied.z * friction);

            // super logic
            this.updateLimbs(this, this instanceof Flutterer);
            this.increaseTravelMotionStats(this.getX() - dx, this.getY() - dy, this.getZ() - dz);
        }
    }
}
