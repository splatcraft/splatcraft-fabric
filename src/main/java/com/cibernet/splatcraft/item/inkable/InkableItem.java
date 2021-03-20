package com.cibernet.splatcraft.item.inkable;

import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;

import java.util.Optional;

public interface InkableItem {
    default String getTranslationKey(String superKey, ItemStack stack) {
        return superKey + "." + ColorUtils.getInkColor(stack);
    }

    default void appendStacks(Item $this, ItemGroup group, DefaultedList<ItemStack> stacks) {
        if ($this.isIn(group)) {
            InkColors.getAll().forEach((id, inkColor) -> stacks.add(ColorUtils.setInkColor(new ItemStack($this, 1, Optional.of(new CompoundTag())), inkColor)));
        }
    }
}
