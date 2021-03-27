package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Collections;

public class SquidBumperEntity extends LivingEntity implements InkableEntity {
    public static final String id = "squid_bumper";

    public final int maxRespawnTime = 60;

    public static final TrackedData<String> INK_COLOR = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Integer> RESPAWN_TIME = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> INKPROOF = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Float> PRE_FLATTEN_DAMAGE = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public SquidBumperEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.pushSpeedReduction = -3.0f;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        dataTracker.startTracking(INK_COLOR, InkColors.NONE.toString());
        dataTracker.startTracking(RESPAWN_TIME, 0);
        dataTracker.startTracking(INKPROOF, false);
        dataTracker.startTracking(PRE_FLATTEN_DAMAGE, 0.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);

        this.inkColorToTag(tag);
        CompoundTag splatcraft = ColorUtils.getOrCreateSplatcraftTag(tag);
        splatcraft.putInt("RespawnTime", this.getRespawnTime());
        splatcraft.putBoolean("Inkproof", this.isInkproof());
        splatcraft.putFloat("PreFlattenDamage", this.getPreFlattenDamage());
    }
    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);

        this.inkColorFromTag(tag);
        CompoundTag splatcraft = ColorUtils.getOrCreateSplatcraftTag(tag);
        this.setRespawnTime(splatcraft.getInt("RespawnTime"));
        this.setInkproof(splatcraft.getBoolean("Inkproof"));
        this.setPreFlattenDamage(splatcraft.getFloat("PreFlattenDamage"));
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return this.isFlattened() ? super.getDimensions(pose).scaled(1.0f, 0.1f) : super.getDimensions(pose);
    }

    @Override
    public void tick() {
        super.tick();
        this.incrementRespawnTime();
    }

    @Override
    protected float turnHead(float bodyRotation, float headRotation) {
        this.prevBodyYaw = this.prevYaw;
        this.bodyYaw = this.yaw;
        return 0.0f;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient && !this.removed) {
            if (!this.isInvulnerableTo(source)) {
                this.breakAndDropItem(source);
                this.remove();
            }
        }

        return false;
    }

    @Override
    public boolean onEntityInked(DamageSource source, float damage, InkColor color) {
        if (!this.removed && !this.isInkproof()) {
            if (!this.isFlattened()) {
                this.flatten(damage);
            }

            return true;
        }

        return false;
    }

    @Override
    protected void pushAway(Entity entity) {
        if (!this.isFlattened()) {
            this.pushAwayFrom(entity);
        }
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (!this.isConnectedThroughVehicle(entity)) {
            if (!entity.noClip && !this.noClip) {
                double x = entity.getX() - this.getX();
                double z = entity.getZ() - this.getZ();
                double xz = MathHelper.absMax(x, z);

                if (xz >= 0.009999999776482582d) {
                    xz = MathHelper.sqrt(xz);
                    x = x / xz;
                    z = z / xz;
                    double d3 = 1.0d / xz;

                    if (d3 > 1.0d) {
                        d3 = 1.0d;
                    }

                    x = x * d3;
                    z = z * d3;
                    x = x * 0.05000000074505806d;
                    z = z * 0.05000000074505806d;
                    x = x * (double)(1.0f - this.pushSpeedReduction);
                    z = z * (double)(1.0f - this.pushSpeedReduction);
                    x *= 3;
                    z *= 3;

                    if (!entity.hasPassengers()) {
                        entity.addVelocity(x, 0.0d, z);
                    }
                }
            }
        }
    }

    public void flatten(float damage) {
        this.setRespawnTime(this.maxRespawnTime);
        this.setPreFlattenDamage(damage);
    }

    private void breakAndDropItem(DamageSource damageSource) {
        if (!damageSource.isSourceCreativePlayer() && damageSource != DamageSource.OUT_OF_WORLD) {
            Block.dropStack(this.world, this.getBlockPos(), this.asItem());
        }
        this.onBreak(damageSource);
    }
    public void onBreak(DamageSource source) {
        this.playBreakSound();
        this.drop(source);
    }
    public void playBreakSound() {
        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_BREAK, this.getSoundCategory(), 1.0f, 1.0f);
    }

    public ItemStack asItem() {
       return ColorUtils.setInkColor(new ItemStack(SplatcraftItems.SQUID_BUMPER), this.getInkColor(), true);
    }

    public void setPreFlattenDamage(float preFlattenDamage) {
        this.dataTracker.set(PRE_FLATTEN_DAMAGE, preFlattenDamage);
    }
    public float getPreFlattenDamage() {
        return this.dataTracker.get(PRE_FLATTEN_DAMAGE);
    }

    public int getRespawnTime() {
        return this.dataTracker.get(RESPAWN_TIME);
    }
    public void setRespawnTime(int respawnTime) {
        this.dataTracker.set(RESPAWN_TIME, respawnTime);

        boolean flattening = respawnTime == this.maxRespawnTime;
        if (respawnTime == 0 || flattening) {
            if (flattening) {
                for (float pitch : new float[]{ 0.0f, 1.0f, 2.0f }) {
                    this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_HURT_DROWN, this.getSoundCategory(), 1.0f, pitch);
                }
            } else {
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_SWIM, this.getSoundCategory(), 1.0f, 2.0f);
            }
        }

        this.calculateDimensions();
    }
    public void incrementRespawnTime() {
        int respawnTime = this.getRespawnTime();
        if (respawnTime > 0) {
            this.setRespawnTime(respawnTime - 1);
        }
    }
    public boolean isFlattened() {
        return this.getRespawnTime() > 0;
    }

    public boolean isInkproof() {
        return this.dataTracker.get(INKPROOF);
    }
    public void setInkproof(boolean inkproof) {
        this.dataTracker.set(INKPROOF, inkproof);
    }

    @Override
    public TrackedData<String> getInkColorTrackedData() {
        return INK_COLOR;
    }
    @Override
    public DataTracker getIEDataTracker() {
        return super.getDataTracker();
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptyList();
    }
    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }
    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {}
    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }
}
