package com.cibernet.splatcraft.block.entity;

import com.cibernet.splatcraft.block.StageBarrierBlock;
import com.cibernet.splatcraft.init.SplatcraftBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;

public class StageBarrierBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {
    public static final String id = StageBarrierBlock.id;

    private int activeTime = 0;
    private final int maxActiveTime = 20;
    private boolean activeTimeIncrementedLastTick = false;

    public StageBarrierBlockEntity() {
        super(SplatcraftBlockEntities.STAGE_BARRIER);
    }

    @Override
    public void tick() {
        if (!activeTimeIncrementedLastTick && this.isActive()) {
            activeTime--;
        }
        this.activeTimeIncrementedLastTick = false;
    }

    public void incrementActiveTime() {
        if (this.activeTime <= this.maxActiveTime) {
            this.activeTime = MathHelper.clamp(this.activeTime + 3, 0, this.maxActiveTime);
            this.activeTimeIncrementedLastTick = true;
        }
    }
    public void resetActiveTime() {
        this.activeTime = maxActiveTime;
    }
    public void setActiveTime(int activeTime) {
        this.activeTime = Math.max(this.activeTime, activeTime);
    }

    public float getMaxActiveTime() {
        return this.maxActiveTime;
    }
    public float getActiveTime() {
        return this.activeTime;
    }
    public boolean isActive() {
        return this.activeTime > 0;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        if (tag.contains("ActiveTime")) {
            activeTime = tag.getInt("ActiveTime");
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("ActiveTime", activeTime);
        return super.toTag(tag);
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(this.getCachedState(), tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.getPos(), 127, this.toClientTag(new CompoundTag()));
    }
}
