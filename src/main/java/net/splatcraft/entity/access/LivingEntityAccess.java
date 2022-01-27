package net.splatcraft.entity.access;

public interface LivingEntityAccess {
    float getScaleForOnEnemyInk();
    float getMaxHealthForOnEnemyInk();

    boolean canFastHeal();
    void resetTicksWithoutDamage();
}
