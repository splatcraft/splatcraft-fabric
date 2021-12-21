package net.splatcraft.entity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.splatcraft.inkcolor.InkType;

import java.util.Optional;

public interface InkEntityAccess {
    InkType getInkType();

    boolean isOnInk();
    boolean isOnOwnInk();
    boolean isOnEnemyInk();

    /**
     * @return whether an entity can enter squid form
     */
    boolean canEnterSquidForm();

    /**
     * @return if an entity can pass through a block due to ink abilities
     */
    boolean doesInkPassing();

    /**
     * @return whether an entity can submerge in ink
     */
    boolean canSubmergeInInk();

    /**
     * @return whether an entity can climb nearby ink
     */
    boolean canClimbInk();

    /**
     * @return an available climbing position
     */
    Optional<BlockPos> getInkClimbingPos();

    Vec3d getInkSplashParticlePos();
}
