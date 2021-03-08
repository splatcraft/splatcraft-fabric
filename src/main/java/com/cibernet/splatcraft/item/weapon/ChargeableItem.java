package com.cibernet.splatcraft.item.weapon;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ChargeableItem {
    float getDischargeSpeed();
    float getChargeSpeed();
    void onRelease(World worldIn, PlayerEntity playerIn, ItemStack stack, float charge);
}
