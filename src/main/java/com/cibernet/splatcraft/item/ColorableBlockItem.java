package com.cibernet.splatcraft.item;

import com.cibernet.splatcraft.init.SplatcraftRegistries;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;

import java.util.Optional;

public class ColorableBlockItem extends BlockItem {
    public ColorableBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            SplatcraftRegistries.INK_COLORS.forEach(inkColor -> stacks.add(ColorUtils.setInkColor(new ItemStack(this.getBlock(), 1, Optional.of(new CompoundTag())), inkColor)));
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey(stack) + "." + ColorUtils.getInkColor(stack);
    }
}
