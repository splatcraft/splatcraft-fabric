package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.entity.InkwellBlockEntity;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import com.cibernet.splatcraft.init.SplatcraftRegistries;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.inkcolor.InkDamageUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface InkableEntity {
    TrackedData<String> INK_COLOR = DataTracker.registerData(InkSquidEntity.class, TrackedDataHandlerRegistry.STRING);

    default void initInkcolorDataTracker() {
        this.getDataTracker().startTracking(INK_COLOR, InkColors.NONE.toString());
    }

    default void setInkColorFromInkwell(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world.getBlockState(pos).getBlock() == SplatcraftBlocks.INKWELL) {
            if (blockEntity instanceof InkwellBlockEntity) {
                InkwellBlockEntity inkwellBlockEntity = (InkwellBlockEntity) world.getBlockEntity(pos);
                if (inkwellBlockEntity != null && ColorUtils.getInkColor(inkwellBlockEntity) != this.getInkColor()) {
                    this.setInkColor(inkwellBlockEntity.getInkColor());
                }
            }
        }
    }

    default void inkColorToTag(CompoundTag tag) {
        CompoundTag splatcraft = new CompoundTag();

        splatcraft.putString("InkColor", this.getInkColor().toString());

        tag.put(Splatcraft.MOD_ID, splatcraft);
    }
    default void inkColorFromTag(CompoundTag tag) {
        CompoundTag splatcraft = tag.getCompound(Splatcraft.MOD_ID);

        this.setInkColor(InkColor.getFromId(splatcraft.getString("InkColor")));
    }

    default void setInkColor(InkColor inkColor) {
        this.getDataTracker().set(INK_COLOR, inkColor.toString());
    }
    default InkColor getInkColor() {
        return SplatcraftRegistries.INK_COLORS.get(new Identifier(this.getDataTracker().get(INK_COLOR)));
    }

    DataTracker getDataTracker();

    @SuppressWarnings("unused")
     default boolean onEntityInked(InkDamageUtils.InkDamageSource source, float damage, InkColor color) {
        return false;
    }
}
