package com.cibernet.splatcraft.item.inkable;

import com.cibernet.splatcraft.init.SplatcraftRegistries;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;

import java.util.Optional;

public abstract class AbstractInkableItem extends Item implements InkableItem {
    public AbstractInkableItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            SplatcraftRegistries.INK_COLORS.forEach(inkColor -> stacks.add(ColorUtils.setInkColor(new ItemStack(this, 1, Optional.of(new CompoundTag())), inkColor)));
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return this.getTranslationKey(super.getTranslationKey(stack), stack);
    }
}
