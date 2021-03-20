package com.cibernet.splatcraft.item.inkable;

import me.andante.chord.item.TabbedItemGroupAppendLogic;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class InkableBlockItem extends BlockItem implements InkableItem, TabbedItemGroupAppendLogic {
    public InkableBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        this.appendStacks(this.asItem(), group, stacks);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return this.getTranslationKey(super.getTranslationKey(stack), stack);
    }
}
