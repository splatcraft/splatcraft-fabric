package net.splatcraft.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.splatcraft.entity.access.InkEntityAccess;
import net.splatcraft.entity.access.LivingEntityAccess;
import net.splatcraft.tag.SplatcraftEntityTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.splatcraft.world.SplatcraftGameRules.*;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    // cap natural regeneration when on enemy ink
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"))
    private void onUpdateHeal(PlayerEntity player, float amount) {
        if (gameRule(player.world, DAMAGE_ON_ENEMY_INK) && SplatcraftEntityTypeTags.HURT_BY_ENEMY_INK.contains(player.getType())) {
            InkEntityAccess inkAccess = (InkEntityAccess) player;
            if (inkAccess.isOnEnemyInk() && (inkAccess.isInSquidForm() || !gameRule(player.world, DAMAGE_ON_ENEMY_INK_ONLY_IN_SQUID_FORM))) {
                LivingEntityAccess livingAccess = (LivingEntityAccess) player;
                float health = player.getHealth();
                float nu = health + amount;
                player.heal(Math.min(nu, livingAccess.getMaxHealthForOnEnemyInk()) - health);
                return;
            }
        }

        player.heal(amount);
    }
}
