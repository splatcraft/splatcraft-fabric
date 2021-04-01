package com.cibernet.splatcraft.block.entity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public abstract class AbstractInkableBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    public static final String id = AbstractInkableBlock.id;

    private InkColor inkColor = InkColors.NONE;

    public AbstractInkableBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public void sync() {
        if (this.world != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(this.pos);
            buf.writeIdentifier(this.getInkColor().id);

            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) this.world, this.pos)) {
                ServerPlayNetworking.send(player, SplatcraftNetworkingConstants.SET_BLOCK_ENTITY_INK_COLOR_PACKET_ID, buf);
            }
        }

        BlockEntityClientSerializable.super.sync();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag splatcraft = ColorUtils.getOrCreateSplatcraftTag(tag);
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
    public boolean setInkColor(InkColor inkColor) {
        if (!this.inkColor.equals(inkColor)) {
            this.inkColor = inkColor;

            if (this.world != null) {
                if (!this.world.isClient) {
                    this.sync();
                }

                this.world.addSyncedBlockEvent(pos, this.getCachedState().getBlock(), 0, 0);
            }

            return true;
        }

        return false;
    }
    public boolean isColored() {
        return !this.inkColor.equals(InkColors.NONE);
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
