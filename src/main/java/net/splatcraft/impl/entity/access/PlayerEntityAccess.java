package net.splatcraft.impl.entity.access;

import net.minecraft.entity.player.PlayerEntity;
import net.splatcraft.api.item.SplatcraftItems;

import java.util.Optional;

public interface PlayerEntityAccess {
    /**
     * Checks a {@link PlayerEntity}'s inventory for a
     * {@link SplatcraftItems#SPLATFEST_BAND}, with
     * respect to configuration.
     *
     * @return whether or not the method had any effect
     */
    boolean checkSplatfestBand();

    /**
     * @return whether the player can enter squid form
     */
    boolean canEnterSquidForm();

    int getWeaponUseCooldown();
    void setWeaponUseCooldown(int time);

    Optional<Float> getMovementSpeedM(float base);
}
