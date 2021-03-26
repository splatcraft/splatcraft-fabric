package com.cibernet.splatcraft.item.weapon.component;

/**
 * Represents the components that make up the properties of a shooter item.
 */
public class ShooterComponent {
    public float consumption;
    public float size;
    public float damage;
    public float firingSpeed;
    public float projectileSpeed;
    public float inaccuracy;

    public ShooterComponent(float consumption, float size, float damage, float firingSpeed, float projectileSpeed, float inaccuracy) {
        this.consumption = consumption;
        this.size = size;
        this.damage = damage;
        this.firingSpeed = firingSpeed;
        this.projectileSpeed = projectileSpeed;
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
}
