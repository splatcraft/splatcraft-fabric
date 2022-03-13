package net.splatcraft.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@FunctionalInterface
public interface UsageSpeedProvider {
    float getMovementSpeedModifier(Context ctx);
    record Context(PlayerEntity player, Hand hand, ItemStack stack, ItemStack otherStack, float base, boolean duplicate, boolean using) {}
}
