package com.cibernet.splatcraft.item.weapon.component;

import com.cibernet.splatcraft.item.weapon.ChargerItem;

/**
 * Represents the components that make up the properties of a charger item.
 */
public class ChargerComponent {
    public final float minConsumption;
    public final float maxConsumption;
    public final int maxCharge;
    public final float chargeSpeed;
    public final int dischargeTime;
    public final float dischargeSpeed;
    public final int cooldownTime;
    public final boolean canAirCharge;
    public final float pierceCharge;
    public final Projectile projectile;

    public ChargerComponent(float minConsumption, float maxConsumption, int maxCharge, int dischargeTime, int cooldownTime, boolean canAirCharge, float pierceCharge, ChargerComponent.Projectile projectile) {
        this.minConsumption = minConsumption;
        this.maxConsumption = maxConsumption;
        this.maxCharge = maxCharge;
        this.chargeSpeed = 1.0f / maxCharge;
        this.dischargeTime = dischargeTime;
        this.dischargeSpeed = 1.0f / dischargeTime;
        this.cooldownTime = cooldownTime;
        this.canAirCharge = canAirCharge;
        this.pierceCharge = pierceCharge;
        this.projectile = projectile;
    }

    public static class Projectile {
        public final float damage;
        public final float size;
        public final float speed;
        public final int lifetime;
        public final boolean chargeAffectsSpeed;

        public Projectile(float damage, float size, float speed, int lifetime, boolean chargeAffectsSpeed) {
            this.size = size;
            this.speed = speed;
            this.lifetime = lifetime;
            this.damage = damage;
            this.chargeAffectsSpeed = chargeAffectsSpeed;
        }
        public Projectile(float damage, float size, float speed, int lifetime) {
            this(damage, size, speed, lifetime, true);
        }

        public static ChargerComponent.Projectile copy(ChargerItem item) {
            return item.component.projectile;
        }
    }
}
