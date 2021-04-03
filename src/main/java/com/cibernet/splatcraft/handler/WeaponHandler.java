package com.cibernet.splatcraft.handler;

import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class WeaponHandler {
    public static void onLivingDamage(LivingEntity entity, float amount) {
        /*if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;

            if (player.getHealth() > 0 && player.getHealth() - amount <= 0) {
                if (ScoreboardHandler.hasColorCriterion(ColorUtils.getPlayerColor(target)))
                    target.getWorldScoreboard().forAllObjectives(ScoreboardHandler.getDeathsAsColor(ColorUtils.getPlayerColor(target)), target.getScoreboardName(), score -> score.increaseScore(1));

                if (event.getSource().getImmediateSource() instanceof PlayerEntity) {
                    PlayerEntity source = (PlayerEntity) event.getSource().getTrueSource();
                    if (ScoreboardHandler.hasColorCriterion(ColorUtils.getPlayerColor(source))) {
                        player.getWorldScoreboard().forAllObjectives(ScoreboardHandler.getColorKills(ColorUtils.getPlayerColor(player)), source.getScoreboardName(), score -> score.increaseScore(1));
                        player.getWorldScoreboard().forAllObjectives(ScoreboardHandler.getKillsAsColor(ColorUtils.getPlayerColor(source)), source.getScoreboardName(), score -> score.increaseScore(1));
                    }
                }
            }
        } TODO */
    }

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
