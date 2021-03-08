package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.item.InkTankArmorItem;
import com.cibernet.splatcraft.item.weapon.AbstractWeaponItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class ClientUtils {
    public static PlayerEntity getClientPlayer() {
        return MinecraftClient.getInstance().player;
    }

    public static boolean showDurabilityBar(ItemStack stack) {
        return getClientPlayer().getStackInHand(Hand.MAIN_HAND).equals(stack) && getDurabilityForDisplay(stack) > 0;
    }

    public static int getDurabilityForDisplay(ItemStack stack) {
        PlayerEntity player = getClientPlayer();

        if (!SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.REQUIRE_INK_TANK)) {
            return 0;
        } else {
            ItemStack chestSlotStack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (chestSlotStack.getItem() instanceof InkTankArmorItem) {
                return (int) (1 - AbstractWeaponItem.getInkAmount(player, stack) / ((InkTankArmorItem) chestSlotStack.getItem()).capacity);
            } else {
                return 1;
            }
        }
    }

    public static boolean canPerformRoll(PlayerEntity entity) {
        Input input = ((ClientPlayerEntity)entity).input;
        return !PlayerDataComponent.Cooldown.hasCooldown(entity) && input.jumping && (input.movementSideways != 0 || input.movementForward != 0);
    }

    public static Vec3d getDodgeRollVector(PlayerEntity entity) {
        Input input = ((ClientPlayerEntity)entity).input;
        return new Vec3d(input.movementSideways, -0.4f, input.movementForward);
    }
}
