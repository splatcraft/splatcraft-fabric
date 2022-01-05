package net.splatcraft.entity.access;

import net.minecraft.entity.player.PlayerEntity;
import net.splatcraft.item.SplatcraftItems;

import java.util.Optional;

public interface PlayerEntityAccess {
    /**
     * Checks a {@link PlayerEntity}'s inventory for a
     * {@link SplatcraftItems#SPLATFEST_BAND}, with
     * respect to configuration.
     *
     * @return whether or not the method had any effect
     */
    boolean updateSplatfestBand();

    /**
     * @return whether the player can enter squid form
     */
    boolean canEnterSquidForm();

    Optional<Float> getMovementSpeedM(float base);
}
