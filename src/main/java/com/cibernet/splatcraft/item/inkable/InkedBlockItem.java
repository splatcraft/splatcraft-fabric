package com.cibernet.splatcraft.item.inkable;

import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.util.TagUtils;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InkedBlockItem extends BlockItem {
    public InkedBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        ColorUtils.appendTooltip(stack, tooltip);
    }

    @Override
    public Text getName(ItemStack stack) {
        return new TranslatableText(this.getTranslationKey(stack), TagUtils.getBlockStateFromInkedBlockItem(stack).getBlock().getName());
    }
}
