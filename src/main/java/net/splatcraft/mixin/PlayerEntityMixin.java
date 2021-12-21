package net.splatcraft.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.entity.InkableCaster;
import net.splatcraft.entity.PlayerEntityAccess;
import net.splatcraft.entity.SplatcraftAttributes;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.util.SplatcraftConstants;
import net.splatcraft.world.SplatcraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Optional;

import static net.splatcraft.util.Events.tickMovementInkableEntity;
import static net.splatcraft.util.SplatcraftConstants.SQUID_FORM_DIMENSIONS;
import static net.splatcraft.util.SplatcraftConstants.SQUID_FORM_SUBMERGED_DIMENSIONS;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Inkable, InkableCaster, PlayerEntityAccess {
    @Shadow public abstract Text getDisplayName();
    @Shadow public abstract PlayerAbilities getAbilities();

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
                              .add(SplatcraftAttributes.INK_SWIM_SPEED, SplatcraftAttributes.INK_SWIM_SPEED.getDefaultValue())
                              .add(SplatcraftAttributes.INK_JUMP_FORCE, SplatcraftAttributes.INK_JUMP_FORCE.getDefaultValue())
        );
    }

    // modify movement speed for squid form
    @Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
    private void getMovementSpeed(CallbackInfoReturnable<Float> cir) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        if (!this.getAbilities().flying) {
            ((PlayerEntityAccess) that).getMovementSpeedM(cir.getReturnValueF())
                                       .ifPresent(cir::setReturnValue);
        }
    }

    // change pose for squid form
    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void onUpdatePose(CallbackInfo ci) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        if (data.isSquid() && !this.getAbilities().flying) {
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
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void onTickMovement(CallbackInfo ci) {
        if (!this.world.isClient) {
            if (this.posLastTick != null) {
                Vec3d lastRenderPos = new Vec3d(this.lastRenderX, this.lastRenderY, this.lastRenderZ);
                tickMovementInkableEntity(this, this.posLastTick.subtract(lastRenderPos));
            }
            this.posLastTick = this.getPos();
        }
    }
}
