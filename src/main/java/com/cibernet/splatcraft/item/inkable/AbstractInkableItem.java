package com.cibernet.splatcraft.item.inkable;

import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColors;
import me.andante.chord.item.TabbedItemGroupAppendLogic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;

import java.util.Optional;

public abstract class AbstractInkableItem extends Item implements InkableItem, TabbedItemGroupAppendLogic {
    public AbstractInkableItem(Settings settings) {
        super(settings);
        SplatcraftItems.addToInkables(this);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            InkColors.getAll().forEach((id, inkColor) -> stacks.add(ColorUtils.setInkColor(new ItemStack(this, 1, Optional.of(new CompoundTag())), inkColor)));
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return this.getTranslationKey(super.getTranslationKey(stack), stack);
    }
}
