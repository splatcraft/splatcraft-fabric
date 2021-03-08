package com.cibernet.splatcraft.item.weapon;

public class FlingComponent {
    protected final float consumption;
    protected final float size;
    protected final float damage;
    protected final float speed;

    public FlingComponent(float consumption, float size, float damage, float speed) {
        this.consumption = consumption;
        this.size = size;
        this.damage = damage;
        this.speed = speed;
    }

    public static FlingComponent copy(RollerItem item) {
        return item.rollerComponent.flingComponent;
    }
}
