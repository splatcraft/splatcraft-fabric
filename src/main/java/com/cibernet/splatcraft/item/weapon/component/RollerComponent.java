package com.cibernet.splatcraft.item.weapon.component;

import com.cibernet.splatcraft.item.weapon.RollerItem;

/**
 * Represents the components that make up the properties of a roller item.
 */
public class RollerComponent {
    public float consumption;
    public int radius;
    public float damage;
    public double speed;
    public boolean brush;
    public Fling fling;

    public RollerComponent(float consumption, int radius, float damage, double speed, boolean brush, Fling fling) {
        this.consumption = consumption;
        this.radius = radius;
        this.damage = damage;
        this.speed = speed;
        this.brush = brush;
        this.fling = fling;
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
    public RollerComponent setFlingComponent(Fling fling) {
        this.fling = fling;
        return this;
    }

    public static RollerComponent copy(RollerItem item) {
        return item.component.copy();
    }
    public RollerComponent copy() {
        return new RollerComponent(this.consumption, this.radius, this.damage, this.speed, this.brush, this.fling.copy());
    }

    public static class Fling {
        public final float consumption;
        public final float size;
        public final float damage;
        public final float speed;

        public Fling(float consumption, float size, float damage, float speed) {
            this.consumption = consumption;
            this.size = size;
            this.damage = damage;
            this.speed = speed;
        }

        public static Fling copy(RollerItem item) {
            return item.component.fling.copy();
        }
        public Fling copy() {
            return new Fling(this.consumption, this.size, this.damage, this.speed);
        }
    }
}
