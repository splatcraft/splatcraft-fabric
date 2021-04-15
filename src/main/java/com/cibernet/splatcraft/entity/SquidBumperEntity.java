package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.entity.enums.SquidBumperDisplayType;
import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.init.SplatcraftTrackedDataHandlers;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.util.StringConstants;
import com.cibernet.splatcraft.util.TagUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;

public class SquidBumperEntity extends LivingEntity implements InkableEntity {
    public static final String id = "squid_bumper";

    protected static final int MAX_LAST_HIT_TIME = 5;

    public static final TrackedData<String> INK_COLOR = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Integer> RESPAWN_TIME = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> MAX_RESPAWN_TIME = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> HURT_DELAY = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> LAST_HIT_TIME = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> INKPROOF = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Float> LAST_DEALT_DAMAGE = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> INK_HEALTH = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> MAX_INK_HEALTH = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<SquidBumperDisplayType> DISPLAY_TYPE = DataTracker.registerData(SquidBumperEntity.class, SplatcraftTrackedDataHandlers.SQUID_BUMPER_DISPLAY_TYPE);

    public SquidBumperEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.pushSpeedReduction = -3.0f;
        this.maxHurtTime = 30;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        dataTracker.startTracking(INK_COLOR, InkColors.NONE.toString());
        dataTracker.startTracking(RESPAWN_TIME, 0);
        dataTracker.startTracking(MAX_RESPAWN_TIME, 30);
        dataTracker.startTracking(HURT_DELAY, 0);
        dataTracker.startTracking(LAST_HIT_TIME, 0);
        dataTracker.startTracking(INKPROOF, false);
        dataTracker.startTracking(LAST_DEALT_DAMAGE, 0.0f);
        float maxInkHealth = 14.0f;
        dataTracker.startTracking(INK_HEALTH, maxInkHealth);
        dataTracker.startTracking(MAX_INK_HEALTH, maxInkHealth);
        dataTracker.startTracking(DISPLAY_TYPE, SquidBumperDisplayType.getDefault());
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);

        this.inkColorToTag(tag);
        CompoundTag splatcraft = TagUtils.getOrCreateSplatcraftTag(tag);
        splatcraft.putInt("RespawnTime", this.getRespawnTime());
        splatcraft.putInt("MaxRespawnTime", this.getMaxRespawnTime());
        splatcraft.putInt("HurtDelay", this.getHurtDelay());
        splatcraft.putInt("LastHitTime", this.getLastHitTime());
        splatcraft.putBoolean("Inkproof", this.isInkproof());
        splatcraft.putFloat("LastDealtDamage", this.getLastDealtDamage());
        splatcraft.putFloat("InkHealth", this.getInkHealth());
        splatcraft.putFloat("MaxInkHealth", this.getMaxInkHealth());
        splatcraft.putString("DisplayType", this.getDisplayType().toString());
    }
    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);

        this.inkColorFromTag(tag);
        CompoundTag splatcraft = TagUtils.getOrCreateSplatcraftTag(tag);
        this.setRespawnTime(splatcraft.getInt("RespawnTime"));
        this.setMaxRespawnTime(splatcraft.getInt("MaxRespawnTime"));
        this.setHurtDelay(splatcraft.getInt("HurtDelay"));
        this.setLastHitTime(splatcraft.getInt("LastHitTime"));
        this.setInkproof(splatcraft.getBoolean("Inkproof"));
        this.setLastDealtDamage(splatcraft.getFloat("LastDealtDamage"));
        this.setInkHealth(splatcraft.getFloat("InkHealth"));
        this.setMaxInkHealth(splatcraft.getFloat("MaxInkHealth"));

        SquidBumperDisplayType displayType = null;
        try {
            displayType = SquidBumperDisplayType.valueOf(splatcraft.getString("DisplayType"));
        } catch (Exception ignored) {}
        this.setDisplayType(displayType == null ? SquidBumperDisplayType.getDefault() : displayType);
    }

    @Override
    public void tick() {
        super.tick();

        this.decrementRespawnTime();
        this.decrementHurtDelay();
        this.decrementLastHitTime();
    }

    @Override
    protected float turnHead(float bodyRotation, float headRotation) {
        this.prevBodyYaw = this.prevYaw;
        this.bodyYaw = this.yaw;
        return 0.0f;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return this.isFlattened()
            ? super.getDimensions(pose)
            .scaled(1.0f, 0.1f)
            : super.getDimensions(pose);
    }

    /*
        DAMAGE & FLATTENING
     */

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient && !this.removed) {
            if (!this.isInvulnerableTo(source) || StringConstants.DAMAGE_SOURCES_ALL.contains(source.getName())) {
                if (source.isSourceCreativePlayer() || this.getLastHitTime() > 0) {
                    this.breakAndDropItem(source);
                    this.remove();
                } else {
                    this.playHitSound();
                    this.setLastHitTime(MAX_LAST_HIT_TIME);
                }
            }
        }

        return false;
    }
    @Override
    public boolean onEntityInked(DamageSource source, float damage, InkColor color) {
        if (!this.removed && !this.isInkproof()) {
            if (!this.isFlattened()) {
                this.inkDamage(damage);
                return true;
            }
        }

        return false;
    }
    public void inkDamage(float damage) {
        float damagedInkHealth = Math.max(this.getInkHealth() - damage, 0.0f);

        if (damagedInkHealth == 0.0f) {
            this.flatten();
        }

        if (!this.world.isClient && this.getHurtDelay() < ((float) this.maxHurtTime - (this.maxHurtTime * 0.0325f))) {
            ServerWorld world = (ServerWorld) this.world;

            int count = (int) ((double) damage * (damage / this.getMaxInkHealth()) * 0.87f);
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, this.getX(), this.getBodyY(1.0d), this.getZ(), count, 0.1d, 0.0d, 0.1d, 0.2d);

            for (float iPitch : new float[]{ 0.0f, 1.0f, 2.0f }) {
                this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_PLAYER_HURT_DROWN, this.getSoundCategory(), 1.0f, iPitch);
            }
        }

        this.resetHurtDelay();
        this.setLastDealtDamage(damage);
        this.setInkHealth(damagedInkHealth);
    }
    public void flatten() {
        this.setRespawnTime(this.getMaxRespawnTime());
    }

    /*
        COLLISION
     */

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
    public void playHitSound() {
        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 1.0f, 1.0f);
    }
    public ItemStack asItem() {
       return ColorUtils.setInkColor(new ItemStack(SplatcraftItems.SQUID_BUMPER), this.getInkColor(), true);
    }

    /*
        LAST DEALT DAMAGE
     */

    public float getLastDealtDamage() {
        return this.dataTracker.get(LAST_DEALT_DAMAGE);
    }
    public void setLastDealtDamage(float preFlattenDamage) {
        this.dataTracker.set(LAST_DEALT_DAMAGE, preFlattenDamage);
    }

    /*
        INK HEALTH
     */

    public float getInkHealth() {
        return this.dataTracker.get(INK_HEALTH);
    }
    public void setInkHealth(float inkHealth) {
        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);
        this.dataTracker.set(INK_HEALTH, Float.parseFloat(df.format(inkHealth)));
    }
    private void incrementInkHealth() {
        float inkHealth = this.getInkHealth();
        if (inkHealth < this.getMaxInkHealth()) {
            this.setInkHealth(Math.min(inkHealth + (this.getMaxInkHealth() / 25.0f), this.getMaxInkHealth()));
        }
    }

    /*
        MAX INK HEALTH
     */

    public float getMaxInkHealth() {
        return this.dataTracker.get(MAX_INK_HEALTH);
    }
    public void setMaxInkHealth(float maxInkHealth) {
        this.dataTracker.set(MAX_INK_HEALTH, maxInkHealth);
    }

    /*
        RESPAWN TIME
     */

    public int getRespawnTime() {
        return this.dataTracker.get(RESPAWN_TIME);
    }
    public void setRespawnTime(int respawnTime) {
        this.dataTracker.set(RESPAWN_TIME, respawnTime);

        boolean flattening = respawnTime == this.getMaxRespawnTime();
        if (respawnTime == 0 || flattening) {
            if (flattening) {
                if (!world.isClient) {
                    for (float iPitch : new float[]{ 0.0f, 1.0f, 2.0f }) {
                        this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_PLAYER_HURT_DROWN, this.getSoundCategory(), 1.0f, iPitch);
                    }
                }
            } else {
                this.setHurtDelay(0);
                if (!world.isClient) {
                    this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_PLAYER_SWIM, this.getSoundCategory(), 1.0f, 2.0f);
                }
            }
        }

        this.calculateDimensions();
    }
    public void decrementRespawnTime() {
        int respawnTime = this.getRespawnTime();
        if (respawnTime > 0) {
            if (respawnTime == 1) {
                this.setInkHealth(this.getMaxInkHealth());
            }

            this.setRespawnTime(respawnTime - 1);
        }
    }
    public boolean isFlattened() {
        return this.getRespawnTime() > 0;
    }

    /*
        MAX RESPAWN TIME
     */

    public int getMaxRespawnTime() {
        return this.dataTracker.get(MAX_RESPAWN_TIME);
    }
    public void setMaxRespawnTime(int maxRespawnTime) {
        this.dataTracker.set(MAX_RESPAWN_TIME, maxRespawnTime);
    }

    /*
        HURT DELAY
     */

    public int getHurtDelay() {
        return this.dataTracker.get(HURT_DELAY);
    }
    public void setHurtDelay(int hurtDelay) {
        this.dataTracker.set(HURT_DELAY, hurtDelay);
    }
    public void decrementHurtDelay() {
        int hurtDelay = this.getHurtDelay();
        if (hurtDelay > 0) {
            this.setHurtDelay(hurtDelay - 1);

            if (hurtDelay <= this.maxHurtTime / 2) {
                this.incrementInkHealth();
            }
        }
    }
    public void resetHurtDelay() {
        this.setHurtDelay(this.maxHurtTime);
    }

    /*
        LAST HIT TIME
     */

    public int getLastHitTime() {
        return this.dataTracker.get(LAST_HIT_TIME);
    }
    public void setLastHitTime(int lastHitTime) {
        this.dataTracker.set(LAST_HIT_TIME, lastHitTime);
    }
    public void decrementLastHitTime() {
        int lastHitTime = this.getLastHitTime();
        if (lastHitTime > 0) {
            this.setLastHitTime(lastHitTime - 1);
        }
    }

    /*
        INKPROOF
     */

    public boolean isInkproof() {
        return this.dataTracker.get(INKPROOF);
    }
    public void setInkproof(boolean inkproof) {
        this.dataTracker.set(INKPROOF, inkproof);
    }

    /*
        DISPLAY TYPE
     */

    public SquidBumperDisplayType getDisplayType() {
        return this.dataTracker.get(DISPLAY_TYPE);
    }
    public void setDisplayType(SquidBumperDisplayType displayType) {
        this.dataTracker.set(DISPLAY_TYPE, displayType);
    }

    /*
        INKABLE ENTITY SUPPORT
     */

    @Override
    public TrackedData<String> getInkColorTrackedData() {
        return INK_COLOR;
    }
    @Override
    public DataTracker getIEDataTracker() {
        return super.getDataTracker();
    }

    /*
        LIVING ENTITY SUPPORT
     */

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
