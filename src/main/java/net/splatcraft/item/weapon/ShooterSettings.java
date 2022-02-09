package net.splatcraft.item.weapon;

import net.minecraft.item.Item;
import net.splatcraft.item.ShooterItem;

import static net.splatcraft.util.SplatcraftUtil.*;

public class ShooterSettings extends WeaponSettings {
    protected final InkConsumptionSettings consumption;
    protected final float usageMobility;
    protected final int initialDelay;
    protected final int fireInterval;
    protected final float projectileSize;
    protected final float projectileSpeed;
    protected final DamageCalculator damage; // TODO

    public ShooterSettings(
        WeaponWeight weight, InkConsumptionSettings consumption, DamageCalculator damage,
        float usageMobility, int initialDelay, int fireInterval,
        float projectileSize, float projectileSpeed
    ) {
        super(weight);
        this.consumption = consumption;
        this.damage = damage;
        this.usageMobility = usageMobility;
        this.initialDelay = frameToTick(initialDelay);
        this.fireInterval = frameToTick(fireInterval);
        this.projectileSize = projectileSize;
        this.projectileSpeed = projectileSpeed;
    }

    public InkConsumptionSettings getConsumption() {
        return this.consumption;
    }

    public float getUsageMobility() {
        return this.usageMobility;
    }

    public int getInitialDelay() {
        return this.initialDelay;
    }

    public int getFireInterval() {
        return this.fireInterval;
    }

    public float getProjectileSize() {
        return this.projectileSize;
    }

    public float getProjectileSpeed() {
        return this.projectileSpeed;
    }

    public DamageCalculator getDamage() {
        return this.damage;
    }

    @Override
    public String toString() {
        return "ShooterSettings{" + "consumption=" + consumption + ", usageMobility=" + usageMobility + ", initialDelay=" + initialDelay + ", fireInterval=" + fireInterval + ", projectileSize=" + projectileSize + ", projectileSpeed=" + projectileSpeed + ", damage=" + damage + ", weight=" + weight + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Item item) {
        return item instanceof ShooterItem shooter ? new Builder(shooter.getShooterSettings()) : new Builder();
    }

    @FunctionalInterface
    public interface DamageCalculator {
        float get(ShooterSettings settings, int age);
    }

    public static class Builder {
        private WeaponWeight weight = WeaponWeight.LIGHTWEIGHT;
        private float usageMobility = 1.0f;
        private int initialDelay = 3;
        private float projectileSize = 1.0f;
        private float projectileSpeed = 1.0f;

        public Builder() {}

        public Builder(Builder builder) {
            this.weight = builder.weight;
            this.usageMobility = builder.usageMobility;
            this.initialDelay = builder.initialDelay;
            this.projectileSize = builder.projectileSize;
            this.projectileSpeed = builder.projectileSpeed;
        }

        public Builder(ShooterSettings settings) {
            this.weight = settings.weight;
            this.usageMobility = settings.usageMobility;
            this.initialDelay = settings.initialDelay;
            this.projectileSize = settings.projectileSize;
            this.projectileSpeed = settings.projectileSpeed;
        }

        public Builder weaponWeight(WeaponWeight weight) {
            this.weight = weight;
            return this;
        }

        public Builder usageMobility(float usageMobility) {
            this.usageMobility = usageMobility;
            return this;
        }

        public Builder initialDelay(int initialDelay) {
            this.initialDelay = initialDelay;
            return this;
        }

        public Builder projectileSize(float projectileSize) {
            this.projectileSize = projectileSize;
            return this;
        }

        public Builder projectileSpeed(float projectileSpeed) {
            this.projectileSpeed = projectileSpeed;
            return this;
        }

        public ShooterSettings build(InkConsumptionSettings consumption, int fireInterval, DamageCalculator damage) {
            return new ShooterSettings(weight, consumption, damage, usageMobility, initialDelay, fireInterval, projectileSize, projectileSpeed);
        }
    }
}
