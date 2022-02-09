package net.splatcraft.item.weapon;

public enum WeaponWeight {
    MIDDLEWEIGHT (1.0f, 1.0f),
    LIGHTWEIGHT  (1.0f + 0.83f, 1.0f + 0.05f),
    HEAVYWEIGHT  (1.0f - 0.83f, 1.0f - 0.10f),
    RAINMAKER    (1.0f - 0.20f, 1.0f - 0.20f);

    private final float movementSpeed, inkSwimSpeed;

    WeaponWeight(float movementSpeed, float inkSwimSpeed) {
        this.movementSpeed = movementSpeed;
        this.inkSwimSpeed = inkSwimSpeed;
    }

    public float getMovementSpeed() {
        return this.movementSpeed;
    }

    public float getInkSwimSpeed() {
        return this.inkSwimSpeed;
    }
}
