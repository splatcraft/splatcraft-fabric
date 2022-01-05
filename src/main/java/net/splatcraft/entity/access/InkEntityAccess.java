package net.splatcraft.entity.access;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public interface InkEntityAccess {
    boolean isInSquidForm();
    boolean isSubmergedInInk();

    boolean isOnInk();
    boolean isOnOwnInk();
    boolean isOnEnemyInk();

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
