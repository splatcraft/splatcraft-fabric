package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.entity.InkableBlockEntity;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.util.TagUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface InkableEntity {
    default void setInkColorFromInkwell(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world.getBlockState(pos).getBlock() == SplatcraftBlocks.INKWELL) {
            if (blockEntity instanceof InkableBlockEntity) {
                InkableBlockEntity inkableBlockEntity = (InkableBlockEntity) world.getBlockEntity(pos);
                if (inkableBlockEntity != null && ColorUtils.getInkColor(inkableBlockEntity) != this.getInkColor()) {
                    this.setInkColor(inkableBlockEntity.getInkColor());
                }
            }
        }
    }

    default void inkColorToTag(CompoundTag tag) {
        CompoundTag splatcraft = TagUtils.getOrCreateSplatcraftTag(tag);
        splatcraft.putString("InkColor", this.getInkColor().toString());

        tag.put(Splatcraft.MOD_ID, splatcraft);
    }
    default void inkColorFromTag(CompoundTag tag) {
        CompoundTag splatcraft = TagUtils.getOrCreateSplatcraftTag(tag);
        this.setInkColor(InkColor.fromNonNull(splatcraft.getString("InkColor")));
    }

    default boolean setInkColor(InkColor inkColor) {
        if (!this.getInkColor().equals(inkColor)) {
            this.getIEDataTracker().set(this.getInkColorTrackedData(), inkColor.toString());
            return true;
        }

        return false;
    }
    default InkColor getInkColor() {
        return InkColors.getNonNull(new Identifier(this.getIEDataTracker().get(this.getInkColorTrackedData())));
    }
    TrackedData<String> getInkColorTrackedData();
    DataTracker getIEDataTracker();

    default boolean onEntityInked(DamageSource source, float damage, InkColor color) {
        return false;
    }
}
