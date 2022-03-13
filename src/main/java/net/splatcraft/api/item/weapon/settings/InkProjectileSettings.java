package net.splatcraft.api.item.weapon.settings;

import static net.splatcraft.api.util.SplatcraftUtil.*;

public class InkProjectileSettings {
    private final float size, speed;
    private final int gravityDelay;

    public InkProjectileSettings(float size, float speed, int gravityDelay) {
        this.size = size;
        this.speed = speed;
        this.gravityDelay = frameToTick(gravityDelay);
    }

    public float getSize() {
        return this.size;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getGravityDelay() {
        return this.gravityDelay;
    }

    @Override
    public String toString() {
        return "InkProjectileSettings{" + "size=" + size + ", speed=" + speed + ", gravityDelay=" + gravityDelay + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private float size = 1.0f;
        private float speed = 1.0f;
        private int gravityDelay = 4;

        public Builder() {}

        public Builder(Builder builder) {
            this.size = builder.size;
            this.speed = builder.speed;
            this.gravityDelay = builder.gravityDelay;
        }

        public Builder(InkProjectileSettings settings) {
            this.size = settings.size;
            this.speed = settings.speed;
            this.gravityDelay = settings.gravityDelay;
        }

        public Builder size(float size) {
            this.size = size;
            return this;
        }

        public Builder speed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder gravityDelay(int gravityDelay) {
            this.gravityDelay = gravityDelay;
            return this;
        }

        public InkProjectileSettings build() {
            return new InkProjectileSettings(size, speed, gravityDelay);
        }
    }
}
