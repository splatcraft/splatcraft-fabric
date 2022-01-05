package net.splatcraft.entity.access;

public interface LivingEntityAccess {
    float getScaleForOnEnemyInk();
    float getMaxHealthForOnEnemyInk();

    void resetTicksWithoutDamage();
}
