package net.splatcraft.item.weapon.settings;

public enum WeaponWeight {
    MIDDLEWEIGHT (1.0f, 1.0f),
    LIGHTWEIGHT  (1.0f + 0.83f, 1.0f + 0.05f),
    HEAVYWEIGHT  (1.0f - 0.83f, 1.0f - 0.10f),
    RAINMAKER    (1.0f - 0.20f, 1.0f - 0.20f);

    private final float usingMovementSpeed, inkSwimSpeed;

    WeaponWeight(float usingMovementSpeed, float inkSwimSpeed) {
        this.usingMovementSpeed = usingMovementSpeed;
        this.inkSwimSpeed = inkSwimSpeed;
    }

    public float getUsingMovementSpeed() {
        return this.usingMovementSpeed;
    }

    public float getInkSwimSpeed() {
        return this.inkSwimSpeed;
    }
}
