package com.cibernet.splatcraft.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public interface EntityTickable {
    default void entityTick(ItemStack stack, ItemEntity entity) {}
}
