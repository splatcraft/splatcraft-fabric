package net.splatcraft.item.weapon.settings;

import net.minecraft.item.Item;

import static net.splatcraft.util.SplatcraftUtil.*;

public class ShooterSettings extends WeaponSettings {
    protected final InkConsumptionSettings consumption;
    protected final InkProjectileSettings projectile;
    protected final DamageCalculator damage;
    protected final float usageMobility;
    protected final int initialDelay;
    protected final int fireInterval;

    public ShooterSettings(
        WeaponWeight weight, InkConsumptionSettings consumption,
        InkProjectileSettings projectile, DamageCalculator damage,
        float usageMobility, int initialDelay, int fireInterval
    ) {
        super(weight);
        this.consumption = consumption;
        this.projectile = projectile;
        this.damage = damage;
        this.usageMobility = usageMobility;
        this.initialDelay = frameToTick(initialDelay);
        this.fireInterval = frameToTick(fireInterval);
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

    public InkProjectileSettings getProjectileSettings() {
        return this.projectile;
    }

    public float getDamage(DamageCalculator.Context ctx) {
        return scaleGameHealth(this.damage.get(ctx));
    }

    @Override
    public String toString() {
        return "ShooterSettings{" + "consumption=" + consumption + ", projectile=" + projectile + ", damage=" + damage + ", usageMobility=" + usageMobility + ", initialDelay=" + initialDelay + ", fireInterval=" + fireInterval + ", weight=" + weight + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Item item) {
        return item instanceof Provider provider ? new Builder(provider.getWeaponSettings()) : new Builder();
    }

    @FunctionalInterface public interface DamageCalculator {
        float get(Context ctx);
        record Context(ShooterSettings settings, int age) {}
    }

    @FunctionalInterface public interface Provider extends WeaponSettings.Provider { @Override ShooterSettings getWeaponSettings(); }

    public static class Builder {
        private WeaponWeight weight = WeaponWeight.LIGHTWEIGHT;
        private InkProjectileSettings projectile = InkProjectileSettings.builder().build();
        private float usageMobility = 1.0f;
        private int initialDelay = 3;

        public Builder() {}

        public Builder(Builder builder) {
            this.weight = builder.weight;
            this.usageMobility = builder.usageMobility;
            this.initialDelay = builder.initialDelay;
            this.projectile = builder.projectile;
        }

        public Builder(ShooterSettings settings) {
            this.weight = settings.weight;
            this.usageMobility = settings.usageMobility;
            this.initialDelay = settings.initialDelay;
            this.projectile = settings.projectile;
        }

        public Builder weaponWeight(WeaponWeight weight) {
            this.weight = weight;
            return this;
        }

        public Builder projectile(InkProjectileSettings settings) {
            this.projectile = settings;
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

        public ShooterSettings build(InkConsumptionSettings consumption, int fireInterval, DamageCalculator damage) {
            return new ShooterSettings(weight, consumption, projectile, damage, usageMobility, initialDelay, fireInterval);
        }
    }
}
