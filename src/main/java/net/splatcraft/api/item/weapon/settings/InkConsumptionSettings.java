package net.splatcraft.api.item.weapon.settings;

import static net.splatcraft.api.util.SplatcraftUtil.*;

public class InkConsumptionSettings {
    protected final float consumption;
    protected final int regenerationCooldown;

    public InkConsumptionSettings(float consumption, int regenerationCooldown) {
        this.consumption = consumption;
        this.regenerationCooldown = frameToTick(regenerationCooldown);
    }

    public float getConsumption() {
        return this.consumption;
    }

    public int getRegenerationCooldown() {
        return this.regenerationCooldown;
    }

    @Override
    public String toString() {
        return "InkConsumptionSettings{" + "consumption=" + consumption + ", regenerationCooldown=" + regenerationCooldown + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private float consumption = 1.0f;
        private int regenerationCooldown = 0;

        public Builder() {}

        public Builder(InkConsumptionSettings settings) {
            this.consumption = settings.consumption;
            this.regenerationCooldown = settings.regenerationCooldown;
        }

        public Builder consumption(float consumption) {
            this.consumption = consumption;
            return this;
        }

        public Builder regenerationCooldown(int cooldown) {
            this.regenerationCooldown = cooldown;
            return this;
        }

        public InkConsumptionSettings build() {
            return new InkConsumptionSettings(consumption, regenerationCooldown);
        }
    }
}
