package com.cibernet.splatcraft.item.inkable;

import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColors;
import me.andante.chord.item.TabbedItemGroupAppendLogic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.Optional;

public abstract class AbstractInkableItem extends Item implements TabbedItemGroupAppendLogic {
    public AbstractInkableItem(Settings settings) {
        super(settings);
        SplatcraftItems.addToInkables(this);
    }

    @Override
    public void appendStacksToTab(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            InkColors.getAll().forEach((id, inkColor) -> stacks.add(ColorUtils.setInkColor(new ItemStack(this, 1, Optional.of(new CompoundTag())), inkColor)));
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        return ColorUtils.getTranslatableTextWithColor(stack, true);
    }
}
