package net.splatcraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.registry.SplatcraftRegistries;

import static net.splatcraft.util.SplatcraftUtil.getInkColorFromStack;
import static net.splatcraft.util.SplatcraftUtil.setInkColorOnStack;

public class InkableBlockItem extends BlockItem {
    public InkableBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (group == SplatcraftItemGroups.INKABLES) {
            for (InkColor inkColor : SplatcraftRegistries.INK_COLOR) stacks.add(setInkColorOnStack(new ItemStack(this), inkColor));
            return;
        }

        super.appendStacks(group, stacks);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        InkColor inkColor = getInkColorFromStack(stack);
        return "%s.%s".formatted(this.getBlock().getTranslationKey(), inkColor.getId());
    }
}
