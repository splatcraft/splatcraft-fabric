package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColors;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InkSquidEntity extends PathAwareEntity implements InkableEntity {
    public static final String id = "ink_squid";

    public InkSquidEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setInkColor(InkColors.NEON_ORANGE);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.BLOCK_HONEY_BLOCK_FALL, 0.15F, 1.0F);
    }

    @Override
    public void travel(Vec3d movementInput) {
        super.travel(movementInput);
        if (this.world.isClient && this.isOnGround() && this.getRandom().nextFloat() <= 0.7F && (this.getVelocity().getX() != 0 || this.getVelocity().getZ() != 0) && InkBlockUtils.isOnInk(this.world, this.getVelocityAffectingPos())) {
            for(int i = 0; i < 2; ++i) {
                ColorUtils.addInkSplashParticle(this.world, this.getVelocityAffectingPos(), new Vec3d(this.getParticleX(0.5D), this.getRandomBodyY() - 0.25D, this.getParticleZ(0.5D)));
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
        this.initInkcolorDataTracker();
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
    public DataTracker getDataTracker() {
        return this.dataTracker;
    }
}
