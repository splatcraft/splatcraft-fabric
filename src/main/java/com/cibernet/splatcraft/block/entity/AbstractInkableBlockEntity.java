package com.cibernet.splatcraft.block.entity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.util.TagUtil;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractInkableBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    public static final String id = "inkable";

    private InkColor inkColor = InkColors.NONE;
    @Nullable private InkColor baseInkColor = null;

    protected AbstractInkableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(tag);
        splatcraft.putString("InkColor", this.getInkColor().toString());

        InkColor baseInkColor = this.getBaseInkColor();
        if (baseInkColor != null) {
            splatcraft.putString("BaseInkColor", this.getBaseInkColor().toString());
        }

        tag.put(Splatcraft.MOD_ID, splatcraft);
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        NbtCompound splatcraft = TagUtil.getOrCreateSplatcraftTag(TagUtil.getBlockEntityTagOrRoot(tag));
        this.setInkColor(InkColor.fromNonNull(splatcraft.getString("InkColor")));
        if (splatcraft.contains("BaseInkColor")) {
            this.setBaseInkColor(InkColor.from(splatcraft.getString("BaseInkColor")));
        }
    }

    public InkColor getInkColor() {
        return this.inkColor;
    }
    public boolean setInkColor(@NotNull InkColor inkColor) {
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
    public boolean isInked() {
        return !this.inkColor.equals(this.hasBaseInkColor() ? this.getBaseInkColor() : InkColors.NONE);
    }

    @Nullable
    public InkColor getBaseInkColor() {
        return this.baseInkColor;
    }
    public boolean setBaseInkColor(@Nullable InkColor baseInkColor) {
        if (this.baseInkColor == null || !this.baseInkColor.equals(baseInkColor)) {
            this.baseInkColor = baseInkColor;

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
    public boolean hasBaseInkColor() {
        InkColor baseInkColor = this.getBaseInkColor();
        return baseInkColor != null && !baseInkColor.equals(InkColors.NONE);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.getPos(), 127 /* annoying fapi constant */, this.toClientTag(new NbtCompound()));
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.readNbt(tag);
    }
    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return this.writeNbt(tag);
    }
}
