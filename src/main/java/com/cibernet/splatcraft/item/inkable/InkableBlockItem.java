package com.cibernet.splatcraft.item.inkable;

import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColors;
import me.andante.chord.item.TabbedItemGroupAppendLogic;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.Optional;

public class InkableBlockItem extends BlockItem implements TabbedItemGroupAppendLogic {
    public InkableBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendStacksToTab(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            InkColors.getAll().forEach((id, inkColor) -> stacks.add(ColorUtil.setInkColor(new ItemStack(this, 1, Optional.of(new CompoundTag())), inkColor)));
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        return ColorUtil.getTranslatableTextWithColor(stack, true);
    }
}
