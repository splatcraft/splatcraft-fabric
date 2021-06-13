package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

public class TagUtil {
    public static NbtCompound getBlockEntityTagOrRoot(ItemStack stack) {
        return stack.getItem() instanceof BlockItem ? stack.getOrCreateSubTag("BlockEntityTag") : stack.getOrCreateTag();
    }
    public static NbtCompound getBlockEntityTagOrRoot(NbtCompound tag) {
        return tag.contains("BlockEntityTag") ? tag.getCompound("BlockEntityTag") : tag;
    }

    public static NbtCompound getOrCreateSplatcraftTag(NbtCompound tag) {
        if (tag != null) {
            NbtCompound root = TagUtil.getBlockEntityTagOrRoot(tag);
            NbtCompound splatcraft = root.getCompound(Splatcraft.MOD_ID);
            if (splatcraft != null) {
                return splatcraft;
            }
        }

        return new NbtCompound();
    }
    public static NbtCompound getOrCreateSplatcraftTag(ItemStack stack) {
        return TagUtil.getOrCreateSplatcraftTag(stack.getTag());
    }

    public static BlockState getBlockStateFromInkedBlockItem(NbtCompound tag) {
        return NbtHelper.toBlockState(TagUtil.getOrCreateSplatcraftTag(tag).getCompound("SavedState"));
    }
    public static BlockState getBlockStateFromInkedBlockItem(ItemStack stack) {
        return TagUtil.getBlockStateFromInkedBlockItem(stack.getTag());
    }
}
