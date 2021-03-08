package com.cibernet.splatcraft.item.weapon;

import org.jetbrains.annotations.Nullable;

/**
 * Represents the components that make up the properties of a roller item.
 */
public class RollerComponent {
    protected float consumption;
    protected int radius;
    protected float damage;
    protected double speed;
    protected boolean brush;
    @Nullable protected FlingComponent flingComponent;

    public RollerComponent(float consumption, int radius, float damage, double speed, boolean brush, @Nullable FlingComponent flingComponent) {
        this.consumption = consumption;
        this.radius = radius;
        this.damage = damage;
        this.speed = speed;
        this.brush = brush;
        this.flingComponent = flingComponent;
    }

    public RollerComponent setDamage(float damage) {
        this.damage = damage;
        return this;
    }
    public RollerComponent setRadius(int radius) {
        this.radius = radius;
        return this;
    }
    public RollerComponent setSpeed(float speed) {
        this.speed = speed;
        return this;
    }
    public RollerComponent setFlingComponent(FlingComponent flingComponent) {
        this.flingComponent = flingComponent;
        return this;
    }

    public static RollerComponent copy(RollerItem item) {
        return item.rollerComponent;
    }
}
