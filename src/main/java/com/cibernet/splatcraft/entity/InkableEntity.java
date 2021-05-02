package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.entity.InkableBlockEntity;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.inkcolor.InkType;
import com.cibernet.splatcraft.util.TagUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface InkableEntity {
    default void setInkColorFromInkwell(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world.getBlockState(pos).getBlock() == SplatcraftBlocks.INKWELL) {
            if (blockEntity instanceof InkableBlockEntity) {
                InkableBlockEntity inkableBlockEntity = (InkableBlockEntity) world.getBlockEntity(pos);
                if (inkableBlockEntity != null && ColorUtil.getInkColor(inkableBlockEntity) != this.getInkColor()) {
                    this.setInkColor(inkableBlockEntity.getInkColor());
                }
            }
        }
    }
    default boolean onEntityInked(DamageSource source, float damage, InkColor color) {
        return false;
    }

    default void inkable_initDataTracker() {
        DataTracker dataTracker = this.inkable_getDataTracker();

        dataTracker.startTracking(this.inkable_getInkColorTrackedData(), InkColors.NONE);
        dataTracker.startTracking(this.inkable_getInkTypeTrackedData(), InkType.NORMAL);
    }
    DataTracker inkable_getDataTracker();

    default void inkable_toTag(CompoundTag tag) {
        CompoundTag splatcraft = TagUtil.getOrCreateSplatcraftTag(tag);
        splatcraft.putString("InkColor", this.getInkColor().toString());
        splatcraft.putString("InkType", this.getInkType().toString());

        tag.put(Splatcraft.MOD_ID, splatcraft);
    }
    default void inkable_fromTag(CompoundTag tag) {
        CompoundTag splatcraft = TagUtil.getOrCreateSplatcraftTag(tag);
        this.setInkColor(InkColor.fromNonNull(splatcraft.getString("InkColor")));

        InkType inkType = null;
        try {
            inkType = InkType.valueOf(splatcraft.getString("InkType"));
        } catch (Exception ignored) {}
        this.setInkType(inkType == null ? InkType.NORMAL : inkType);
    }

    TrackedData<InkColor> inkable_getInkColorTrackedData();
    default InkColor getInkColor() {
        return this.inkable_getDataTracker().get(this.inkable_getInkColorTrackedData());
    }
    default boolean setInkColor(InkColor inkColor) {
        if (!this.getInkColor().equals(inkColor)) {
            this.inkable_getDataTracker().set(this.inkable_getInkColorTrackedData(), inkColor);
            return true;
        }

        return false;
    }

    TrackedData<InkType> inkable_getInkTypeTrackedData();
    default InkType getInkType() {
        return this.inkable_getDataTracker().get(this.inkable_getInkTypeTrackedData());
    }
    default void setInkType(InkType inkType) {
        if (!this.getInkType().equals(inkType)) {
            this.inkable_getDataTracker().set(this.inkable_getInkTypeTrackedData(), inkType);
        }
    }
}
