package com.cibernet.splatcraft.block.entity;

import com.cibernet.splatcraft.block.StageBarrierBlock;
import com.cibernet.splatcraft.init.SplatcraftBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class StageBarrierBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    public static final String id = StageBarrierBlock.id;

    private int activeTime = 0;
    private final int maxActiveTime = 20;
    private boolean activeTimeIncrementedLastTick = false;

    public StageBarrierBlockEntity(BlockPos pos, BlockState state) {
        super(SplatcraftBlockEntities.STAGE_BARRIER, pos, state);
    }

    public static <E extends BlockEntity> void serverTick(World world, BlockPos blockPos, BlockState blockState, E e) {
        if (e instanceof StageBarrierBlockEntity blockEntity) {
            if (!blockEntity.activeTimeIncrementedLastTick && blockEntity.isActive()) {
                blockEntity.activeTime--;
            }
            blockEntity.activeTimeIncrementedLastTick = false;
        }
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
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        if (tag.contains("ActiveTime")) {
            activeTime = tag.getInt("ActiveTime");
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putInt("ActiveTime", activeTime);
        return super.writeNbt(tag);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.writeNbt(new NbtCompound());
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return this.writeNbt(tag);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.getPos(), 127, this.toClientTag(new NbtCompound()));
    }
}
