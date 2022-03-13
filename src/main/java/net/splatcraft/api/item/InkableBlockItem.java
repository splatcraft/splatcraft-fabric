package net.splatcraft.api.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.api.registry.SplatcraftRegistries;

public class InkableBlockItem extends BlockItem {
    public InkableBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (group == SplatcraftItemGroups.INKABLES) {
            for (InkColor inkColor : SplatcraftRegistries.INK_COLOR) {
                ItemStack stack = new ItemStack(this);
                ((Inkable) (Object) stack).setInkColor(inkColor);
                stacks.add(stack);
            }
        } else super.appendStacks(group, stacks);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        InkColor inkColor = ((Inkable) (Object) stack).getInkColor();
        return "%s.%s".formatted(this.getBlock().getTranslationKey(), inkColor.getId());
    }
}
