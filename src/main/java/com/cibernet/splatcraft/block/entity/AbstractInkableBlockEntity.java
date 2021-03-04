package com.cibernet.splatcraft.block.entity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

public abstract class AbstractInkableBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    public static final String id = AbstractInkableBlock.id;

    private InkColor inkColor = InkColors.NONE;

    public AbstractInkableBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag splatcraft = tag.getCompound(Splatcraft.MOD_ID);
        if (splatcraft == null) splatcraft = new CompoundTag();

        splatcraft.putString("InkColor", this.inkColor.toString());

        tag.put(Splatcraft.MOD_ID, splatcraft);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        CompoundTag splatcraft = tag.getCompound(Splatcraft.MOD_ID);

        this.inkColor = InkColor.getFromId(splatcraft.getString("InkColor"));
    }

    public InkColor getInkColor() {
        return this.inkColor;
    }
    public void setInkColor(InkColor inkColor) {
        if (this.world != null && !this.world.isClient) {
            this.sync();
        }
        this.inkColor = inkColor;
    }
    public boolean isColored() {
        return this.inkColor != InkColors.NONE;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.getPos(), this.getRawId(), this.toTag(new CompoundTag()));
    }
    public abstract int getRawId();

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(this.getCachedState(), tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }
}
