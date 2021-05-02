package com.cibernet.splatcraft.handler;

import com.cibernet.splatcraft.component.Charge;
import com.cibernet.splatcraft.component.Cooldown;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import net.minecraft.entity.player.PlayerEntity;

public class WeaponHandler {
    public static void onPlayerTick(PlayerEntity player) {
        /*if (PlayerDataComponent.hasCooldown(player)) {
            player.inventory.selectedSlot = PlayerDataComponent.getCooldown(player).getSlotIndex();
        }*/

        boolean canUseWeapon = true;

        if (Cooldown.tick(player)) {
            Cooldown cooldown = PlayerDataComponent.getCooldown(player);
            canUseWeapon = !cooldown.preventsWeaponUse();
        }

        if (!canUseWeapon && player.isUsingItem()) {
            Charge.dischargeWeapon(player);
        }
    }
}
