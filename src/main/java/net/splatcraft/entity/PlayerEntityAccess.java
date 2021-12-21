package net.splatcraft.entity;

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

    Optional<Float> getMovementSpeedM(float base);
}
