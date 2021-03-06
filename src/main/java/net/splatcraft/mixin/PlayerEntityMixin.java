package net.splatcraft.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.splatcraft.api.component.PlayerDataComponent;
import net.splatcraft.api.entity.SplatcraftAttributes;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.InkType;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.api.item.SplatcraftItems;
import net.splatcraft.api.item.UsageSpeedProvider;
import net.splatcraft.api.item.weapon.WeaponItem;
import net.splatcraft.api.util.SplatcraftConstants;
import net.splatcraft.impl.client.config.ClientConfig;
import net.splatcraft.impl.entity.PackedInput;
import net.splatcraft.impl.entity.access.InkEntityAccess;
import net.splatcraft.impl.entity.access.InkableCaster;
import net.splatcraft.impl.entity.access.InputPlayerEntityAccess;
import net.splatcraft.impl.entity.access.LivingEntityAccess;
import net.splatcraft.impl.entity.access.PlayerEntityAccess;
import net.splatcraft.impl.util.IntegrationUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Optional;

import static net.moddingplayground.frame.api.gamerules.v0.SynchronizedBooleanGameRuleRegistry.*;
import static net.splatcraft.api.particle.SplatcraftParticleType.*;
import static net.splatcraft.api.util.SplatcraftConstants.*;
import static net.splatcraft.api.world.SplatcraftGameRules.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Inkable, InkableCaster, PlayerEntityAccess, InkEntityAccess {
    @Shadow @Final private PlayerAbilities abilities;

    @Shadow public abstract void increaseTravelMotionStats(double dx, double dy, double dz);
    @Shadow public abstract PlayerInventory getInventory();

    @Unique private int enemyInkSquidFormTicks;
    @Unique private int lastWeaponUseTime = -100;

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    @Override
    public boolean isInSquidForm() {
        PlayerEntity that = (PlayerEntity) (Object) this;
        PlayerDataComponent data = PlayerDataComponent.get(that);
        return data.isSquid();
    }

    @Unique
    @Override
    public boolean isSubmergedInInk() {
        PlayerEntity that = (PlayerEntity) (Object) this;
        PlayerDataComponent data = PlayerDataComponent.get(that);
        return data.isSubmerged();
    }

    @Unique
    @Override
    public InkColor getInkColor() {
        PlayerEntity that = (PlayerEntity) (Object) this;
        PlayerDataComponent data = PlayerDataComponent.get(that);
        return data.getInkColor();
    }

    @Unique
    @Override
    public boolean setInkColor(InkColor inkColor) {
        PlayerEntity that = (PlayerEntity) (Object) this;
        PlayerDataComponent data = PlayerDataComponent.get(that);
        return data.setInkColor(inkColor);
    }

    @Unique
    @Override
    public InkType getInkType() {
        PlayerEntity that = (PlayerEntity) (Object) this;
        PlayerDataComponent data = PlayerDataComponent.get(that);
        return data.hasSplatfestBand() ? InkType.GLOWING : InkType.NORMAL;
    }

    @Unique
    @Override
    public boolean setInkType(InkType inkType) {
        return false;
    }

    @Unique
    @Override
    public Text getTextForCommand() {
        return this.getDisplayName();
    }

    @SuppressWarnings("unchecked")
    @Unique
    @Override
    public <T extends Entity & Inkable> T toInkable() {
        return (T) this;
    }

    @Unique
    @Override
    public boolean canSubmergeInInk() {
        return this.isInSquidForm() && !this.isSpectator() && (this.isOnOwnInk() || this.canClimbInk());
    }

    @Unique
    @Override
    public boolean checkSplatfestBand() {
        PlayerEntity that = (PlayerEntity) (Object) this;
        PlayerDataComponent data = PlayerDataComponent.get(that);

        if (gameRule(this.world, SPLATFEST_BAND_MUST_BE_HELD)) {
            for (Hand hand : Hand.values()) {
                ItemStack stack = this.getStackInHand(hand);
                if (stack.isOf(SplatcraftItems.SPLATFEST_BAND)) return data.setHasSplatfestBand(true);
            }
        } else {
            PlayerInventory inventory = this.getInventory();
            if (inventory.containsAny(Collections.singleton(SplatcraftItems.SPLATFEST_BAND))) return data.setHasSplatfestBand(true);
        }

        if (IntegrationUtil.Trinkets.checkSplatfestBand(that)) return data.setHasSplatfestBand(true);

        return data.setHasSplatfestBand(false);
    }

    @Unique
    @Override
    public boolean canEnterSquidForm() {
        return !this.isCoolingDownSquidForm() && !this.hasVehicle();
    }

    @Unique
    @Override
    public int getWeaponUseCooldown() {
        return this.lastWeaponUseTime;
    }

    @Unique
    @Override
    public void setWeaponUseCooldown(int time) {
        this.lastWeaponUseTime = time;
    }

    @Unique
    @Override
    public Optional<Float> getMovementSpeedM(float base) {
        PlayerEntity that = (PlayerEntity) (Object) this;
        float nu = base;

        for (Hand hand : Hand.values()) {
            ItemStack stack = this.getStackInHand(hand);
            if (stack.getItem() instanceof UsageSpeedProvider provider) {
                ItemStack other = this.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
                boolean duplicate = hand == Hand.OFF_HAND && other.isItemEqual(stack);
                boolean using = this.getActiveItem() == stack;
                nu = provider.getMovementSpeedModifier(new UsageSpeedProvider.Context(that, hand, stack, other, nu, duplicate, using));
            }
        }

        if (this.isSubmergedInInk()) {
            nu *= (this.getAttributeValue(SplatcraftAttributes.INK_SWIM_SPEED) * 10) / (this.isSneaking() ? 1.5f : 1);
        } else {
            if (this.isOnEnemyInk() && INSTANCE.get(this.world, ENEMY_INK_SLOWNESS)) nu *= 0.475f;
        }

        return base != nu ? Optional.of(nu) : Optional.empty();
    }

    /**
     * Cancels exhaustion if in squid form.
     */
    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    private void onAddExhaustion(float exhaustion, CallbackInfo ci) {
        if (this.isInSquidForm()) ci.cancel();
    }

    /**
     * Disables food healing under certain conditions.
     */
    @Inject(method = "canFoodHeal", at = @At("RETURN"), cancellable = true)
    private void onCanFoodHeal(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            PlayerEntity that = (PlayerEntity) (Object) this;
            LivingEntityAccess access = ((LivingEntityAccess) that);
            if (this.isInSquidForm() || (!access.canFastHeal() && gameRule(this.world, DISABLE_FOOD_HEAL_AFTER_DAMAGE))) cir.setReturnValue(false);
        }
    }

    /**
     * Modifies attributes for squid form.
     */
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void onCreatePlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(cir.getReturnValue()
                              .add(SplatcraftAttributes.INK_SWIM_SPEED)
                              .add(SplatcraftAttributes.INK_JUMP_FORCE)
        );
    }

    /**
     * Modifies movement speed for squid form.
     */
    @Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        PlayerEntity that = (PlayerEntity) (Object) this;
        if (!this.abilities.flying) {
            ((PlayerEntityAccess) that).getMovementSpeedM(cir.getReturnValueF())
                                       .ifPresent(cir::setReturnValue);
        }
    }

    /**
     * Modifies pose for squid form.
     */
    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void onUpdatePose(CallbackInfo ci) {
        if (this.isInSquidForm() && !this.abilities.flying) {
            this.setPose(EntityPose.SWIMMING);
            ci.cancel();
        }
    }

    /**
     * Modifies dimensions for squid form.
     */
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void onGetDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (this.isInSquidForm()) cir.setReturnValue(this.isSubmergedInInk() ? SQUID_FORM_SUBMERGED_DIMENSIONS : SQUID_FORM_DIMENSIONS);
    }

    /**
     * Modifies eye height for squid form.
     */
    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        try {
            if (this.isInSquidForm()) cir.setReturnValue(SplatcraftConstants.getEyeHeight(this.isSubmergedInInk()));
        } catch (NullPointerException ignored) {}
    }

    /**
     * Modifies jump force for submersion.
     */
    @Inject(method = "jump", at = @At("TAIL"))
    private void onJump(CallbackInfo ci) {
        PlayerEntity that = (PlayerEntity) (Object) this;
        if (this.isSubmergedInInk()) {
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

    @Unique private Vec3d posLastTick;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity that = (PlayerEntity) (Object) this;
        PlayerDataComponent data = PlayerDataComponent.get(that);

        boolean sidedRuns = !this.world.isClient || !optimiseDesyncSetting();
        Vec3d pos = this.getPos();

        // leave squid form if on enemy ink and time passed
        this.enemyInkSquidFormTicks = this.shouldTickEnemyInkSquidForm() ? this.enemyInkSquidFormTicks + 1 : 0;
        if (this.enemyInkSquidFormTicks > 16) this.enemyInkSquidFormTicks = 0;

        if (this.world.isClient) {
            if (this.isCoolingDownSquidForm()) {
                Random rand = this.random;
                Vec3d offset = new Vec3d((rand.nextDouble() - 0.5d) / 4, 0.0d, (rand.nextDouble() - 0.5d) / 4);
                inkSplash(this.world, this, pos.add(offset), 0.25f);
            }
        }

        if (sidedRuns) {
            // check for submersion
            data.setSubmerged(this.canSubmergeInInk());
        }

        // tick weapons
        this.setWeaponUseCooldown(Math.max(this.getWeaponUseCooldown() - 1, 0));

        // tick movement
        if (this.posLastTick != null) tickInkable(this, this.posLastTick.subtract(pos));
        this.posLastTick = pos;
    }

    @Unique
    private boolean shouldTickEnemyInkSquidForm() {
        if (!INSTANCE.get(this.world, LEAVE_SQUID_FORM_ON_ENEMY_INK)) return false;
        return (this.isInSquidForm() || this.isCoolingDownSquidForm()) && this.isOnEnemyInk();
    }

    @Unique
    public boolean isCoolingDownSquidForm() {
        return this.enemyInkSquidFormTicks > 6;
    }

    @Unique
    @Environment(EnvType.CLIENT)
    private static boolean optimiseDesyncSetting() {
        return ClientConfig.INSTANCE.optimiseDesync.getValue();
    }

    /**
     * Disables attack cooldown when holding a weapon.
     */
    @Inject(method = "resetLastAttackedTicks", at = @At("HEAD"), cancellable = true)
    private void onResetLastAttackedTicks(CallbackInfo ci) {
        if (this.getMainHandStack().getItem() instanceof WeaponItem) ci.cancel();
    }

    /**
     * Modifies travel logic if climbing ink.
     */
    @SuppressWarnings("deprecation")
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        if (this.isInSquidForm() && this.canClimbInk()) {
            ci.cancel();

            double dx = this.getX();
            double dy = this.getY();
            double dz = this.getZ();

            PackedInput input = ((InputPlayerEntityAccess) this).getPackedInput();
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
            double upward = (input.jumping() || input.forward() || input.sideways() ? 0.3d : 0.0d);
            double y = Math.min(applied.y + upward, this.getMovementSpeed());

            // apply gravity
            if (this.world.isChunkLoaded(vpos)) {
                if (!this.hasNoGravity()) y -= gravity;
            } else {
                y = this.getY() > (double)this.world.getBottomY() ? -0.1d : 0.0d;
            }

            // speed up/slow down speed conditionally
            if (input.sneaking() && y < 0) y /= 1.5d;
            if (input.jumping()) y = Math.abs(y * 1.25d);

            // set velocity
            double friction = this.onGround ? slipperiness * 0.91d : 0.91d;
            this.setVelocity(applied.x * friction, y * 0.98d, applied.z * friction);

            // super logic
            this.updateLimbs(this, this instanceof Flutterer);
            this.increaseTravelMotionStats(this.getX() - dx, this.getY() - dy, this.getZ() - dz);
        }
    }
}
