package com.cibernet.splatcraft.entity.enums;

import com.cibernet.splatcraft.entity.SquidBumperEntity;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * How a {@link SquidBumperEntity}'s name should display.
 */
public enum SquidBumperDisplayType {
    /**
     * The last dealt ink damage to a {@link SquidBumperEntity}.
     */
    LAST_DEALT_DAMAGE,
    /**
     * A {@link SquidBumperEntity}'s ink damage health.
     */
    INK_HEALTH,
    /**
     * A {@link SquidBumperEntity}'s ink damage health out of its max ink damage health.
     */
    INK_HEALTH_COMPARISON,
    /**
     * A {@link SquidBumperEntity}'s ink damage health percentage out of its max ink damage health.
     */
    INK_HEALTH_PERCENTAGE,
    /**
     * A {@link SquidBumperEntity}'s max ink damage health minus its ink damage health.
     */
    INK_HEALTH_DELTA,
    /**
     * A {@link SquidBumperEntity}'s time before respawning.
     */
    RESPAWN_TIME,
    /**
     * A {@link SquidBumperEntity}'s hurt forget delay.
     */
    HURT_DELAY;

    public static SquidBumperDisplayType getDefault() {
        return LAST_DEALT_DAMAGE;
    }

    public String getSnakeCase() {
        return this.toString().toLowerCase();
    }
    public <T extends SquidBumperEntity> Object getObjectToDisplay(T entity) {
        switch (this) {
            default:
            case LAST_DEALT_DAMAGE:
                return entity.getLastDealtDamage();
            case INK_HEALTH:
            case INK_HEALTH_COMPARISON:
                return entity.getInkHealth();
            case INK_HEALTH_DELTA:
                return entity.getMaxInkHealth() - entity.getInkHealth();
            case INK_HEALTH_PERCENTAGE:
                DecimalFormat df = new DecimalFormat("##.##");
                df.setRoundingMode(RoundingMode.DOWN);
                return df.format((entity.getInkHealth() / entity.getMaxInkHealth()) * 100);
            case RESPAWN_TIME:
                return entity.getRespawnTime();
            case HURT_DELAY:
                return entity.getHurtDelay();
        }
    }
    public <T extends SquidBumperEntity> Object getAltObjectToDisplay(T entity) {
        return this == INK_HEALTH_COMPARISON ? entity.getMaxInkHealth() : "";
    }

    public boolean alwaysDisplay() {
        return this != SquidBumperDisplayType.LAST_DEALT_DAMAGE;
    }
}
