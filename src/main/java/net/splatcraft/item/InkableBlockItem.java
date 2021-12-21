package net.splatcraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.registry.SplatcraftRegistries;

public class InkableBlockItem extends BlockItem {
    public InkableBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (group == SplatcraftItemGroups.INKABLES) {
            for (InkColor inkColor : SplatcraftRegistries.INK_COLOR) {
                ItemStack stack = new ItemStack(this);
                Inkable.class.cast(stack).setInkColor(inkColor);
                stacks.add(stack);
            }
            return;
        }

        super.appendStacks(group, stacks);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String getTranslationKey(ItemStack stack) {
        InkColor inkColor = Inkable.class.cast(stack).getInkColor();
        return "%s.%s".formatted(this.getBlock().getTranslationKey(), inkColor.getId());
    }
}
