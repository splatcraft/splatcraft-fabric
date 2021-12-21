package net.splatcraft.entity;

import net.splatcraft.inkcolor.InkType;

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
     * @return whether an entity can submerge in ink
     */
    boolean canSubmergeInInk();

    /**
     * @return if an entity can pass through a block due to ink abilities
     */
    boolean doesInkPassing();
}
