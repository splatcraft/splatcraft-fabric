package com.cibernet.splatcraft.entity.damage;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.entity.damage.vanilla.PublicDamageSource;
import net.minecraft.entity.damage.DamageSource;

public class SplatcraftDamageSources {
    public static final DamageSource VOID_DAMAGE = new PublicDamageSource(createName("outOfBounds")).setBypassesArmor();
    public static final DamageSource ENEMY_INK = new PublicDamageSource(createName("enemyInk")).setBypassesArmor();
    public static final DamageSource WATER = new PublicDamageSource(createName("dissolve")).setBypassesArmor();

    private static String createName(String id) {
        return Splatcraft.MOD_ID + "." + id;
    }
}
