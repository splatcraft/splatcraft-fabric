package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.item.InkTankArmorItem;
import com.cibernet.splatcraft.item.weapon.AbstractWeaponItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InkItemUtil {
    public static float getInkAmount(LivingEntity player, ItemStack weapon) {
        if (!SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.REQUIRE_INK_TANK)) {
            return Float.MAX_VALUE;
        } else {
            ItemStack tank = player.getEquippedStack(EquipmentSlot.CHEST);
            if (tank.getItem() instanceof InkTankArmorItem) {
                return InkTankArmorItem.getInkAmount(tank, weapon);
            }
        }

        return 0.0f;
    }

    public static float getInkReductionAmount(PlayerEntity player, AbstractWeaponItem weapon, float data) {
        return player.getEquippedStack(EquipmentSlot.CHEST).getMaxDamage() * (weapon.getInkConsumption(data) / 100);
    }

    public static void reduceInk(PlayerEntity player, float amount) {
        ItemStack tank = player.getEquippedStack(EquipmentSlot.CHEST);
        float inkAmount = InkTankArmorItem.getInkAmount(tank);

        if (!player.world.isClient) {
            if (SplatcraftGameRules.getBoolean(player.world, SplatcraftGameRules.REQUIRE_INK_TANK) && tank.getItem() instanceof InkTankArmorItem) {
                InkTankArmorItem.setInkAmount(tank, inkAmount - amount);
            }
        }
    }

    public static float getInkTankCapacity(PlayerEntity player) {
        return InkItemUtil.getInkTankCapacity(player.getEquippedStack(EquipmentSlot.CHEST));
    }
    public static float getInkTankCapacity(ItemStack stack) {
        if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof InkTankArmorItem) {
                return ((InkTankArmorItem) item).capacity;
            }
        }

        return -1;
    }
}
