package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.handler.PlayerHandler;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColors;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InkSquidEntity extends PathAwareEntity implements InkableEntity {
    public static final String id = "ink_squid";

    public static final TrackedData<String> INK_COLOR = DataTracker.registerData(InkSquidEntity.class, TrackedDataHandlerRegistry.STRING);

    public InkSquidEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setInkColor(InkColors.NEON_ORANGE);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.BLOCK_HONEY_BLOCK_FALL, 0.15f, 1.0f);
    }

    @Override
    public void travel(Vec3d movementInput) {
        super.travel(movementInput);
        if (this.world.isClient && this.isOnGround() && this.getRandom().nextFloat() <= 0.7f && (this.getVelocity().getX() != 0 || this.getVelocity().getZ() != 0) && PlayerHandler.canSwim(this.world, this.getVelocityAffectingPos())) {
            for (int i = 0; i < 2; ++i) {
                ColorUtils.addInkSplashParticle(this.world, this.getVelocityAffectingPos(), new Vec3d(this.getParticleX(0.5d), this.getRandomBodyY() - 0.25d, this.getParticleZ(0.5d)));
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_COD_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COD_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_COD_HURT;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(INK_COLOR, InkColors.NONE.toString());
    }

    @Override
    public TrackedData<String> getInkColorTrackedData() {
        return INK_COLOR;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.setInkColorFromInkwell(this.world, this.getVelocityAffectingPos());
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        this.inkColorToTag(tag);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inkColorFromTag(tag);
    }

    @Override
    public DataTracker getIEDataTracker() {
        return this.dataTracker;
    }
}
