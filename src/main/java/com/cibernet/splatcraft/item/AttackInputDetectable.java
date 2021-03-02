package com.cibernet.splatcraft.item;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface AttackInputDetectable {
    void onAttack(ServerPlayerEntity player, ItemStack stack);
}
