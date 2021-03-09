package com.cibernet.splatcraft.item.weapon;

/**
 * Represents the components that make up the properties of a shooter item.
 */
public class ShooterComponent {
    protected float consumption;
    protected float size;
    protected float damage;
    protected float firingSpeed;
    protected float speed;
    protected float inaccuracy;

    public ShooterComponent(float consumption, float size, float damage, float firingSpeed, float speed, float inaccuracy) {
        this.consumption = consumption;
        this.size = size;
        this.damage = damage;
        this.firingSpeed = firingSpeed;
        this.speed = speed;
        this.inaccuracy = inaccuracy;
    }

    public ShooterComponent setDamage(float damage) {
        this.damage = damage;
        return this;
    }
    public ShooterComponent setSize(int size) {
        this.size = size;
        return this;
    }

    public static ShooterComponent copy(ShooterItem item) {
        return item.shooterComponent;
    }
}
