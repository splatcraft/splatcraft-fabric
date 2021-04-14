package com.cibernet.splatcraft.handler;

import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import net.minecraft.entity.player.PlayerEntity;

public class WeaponHandler {
    public static void onPlayerTick(PlayerEntity player) {
        if (PlayerDataComponent.Cooldown.hasCooldown(player)) {
            player.inventory.selectedSlot = PlayerDataComponent.Cooldown.getCooldown(player).getSlotIndex();
        }

        boolean canUseWeapon = true;

        LazyPlayerDataComponent lazyData = LazyPlayerDataComponent.getComponent(player);
        if (PlayerDataComponent.Cooldown.shrinkCooldownTime(player, 1) != null) {
            PlayerDataComponent.Cooldown cooldown = PlayerDataComponent.Cooldown.getCooldown(player);
            lazyData.setIsSquid(false);
            canUseWeapon = !cooldown.preventWeaponUse();
        }

        if (!(canUseWeapon && player.getActiveHand() != null && player.getItemUseTimeLeft() > 0) && PlayerDataComponent.Charge.canDischarge(player) || lazyData.isSquid()) {
            PlayerDataComponent.Charge.dischargeWeapon(player);
        }
    }
}
