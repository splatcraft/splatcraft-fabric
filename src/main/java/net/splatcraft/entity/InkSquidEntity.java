package net.splatcraft.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.splatcraft.entity.access.InkableCaster;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.util.TrackedDataUtil;

import static net.splatcraft.util.SplatcraftConstants.*;

public class InkSquidEntity extends MobEntity implements Inkable, InkableCaster {
    public static final TrackedData<String> INK_COLOR = DataTracker.registerData(InkSquidEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Integer> INK_TYPE = DataTracker.registerData(InkSquidEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public InkSquidEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(INK_COLOR, InkColors.getDefault().toString());
        this.dataTracker.startTracking(INK_TYPE, InkType.NORMAL.ordinal());
    }

    @Override
    public InkColor getInkColor() {
        return TrackedDataUtil.inkColor(this.dataTracker, INK_COLOR);
    }

    @Override
    public boolean setInkColor(InkColor inkColor) {
        if (this.getInkColor().equals(inkColor)) return false;
        TrackedDataUtil.inkColor(this.dataTracker, INK_COLOR, inkColor);
        return true;
    }

    @Override
    public InkType getInkType() {
        return TrackedDataUtil.inkType(this.dataTracker, INK_TYPE);
    }

    @Override
    public boolean setInkType(InkType inkType) {
        if (this.getInkType().equals(inkType)) return false;
        TrackedDataUtil.inkType(this.dataTracker, INK_TYPE, inkType);
        return true;
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
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        tickInkable(this, this.getVelocity().multiply(1.0d, 0.0d, 1.0d));
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.BLOCK_HONEY_BLOCK_FALL, 0.15f, 1.0f);
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
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString(NBT_INK_COLOR, this.getInkColor().toString());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.setInkColor(InkColor.fromString(nbt.getString(NBT_INK_COLOR)));
    }
}
