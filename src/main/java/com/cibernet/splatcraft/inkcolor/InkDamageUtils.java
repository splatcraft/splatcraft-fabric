package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.entity.InkableEntity;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.world.World;

public class InkDamageUtils {
    public static void splatDamage(World world, LivingEntity target, float damage, InkColor inkColor, Entity source, boolean damageMobs) {
        InkDamageUtils.attackDamage(world, target, damage, inkColor, source, damageMobs, "splat");
    }

    public static void rollDamage(World world, LivingEntity target, float damage, InkColor inkColor, Entity source, boolean damageMobs) {
        InkDamageUtils.attackDamage(world, target, damage, inkColor, source, damageMobs, "roll");
    }

    public static void attackDamage(World world, LivingEntity target, float damage, InkColor inkColor, Entity source, boolean damageMobs, String damageSourceId) {
        InkDamageUtils.attackDamage(world, target, damage, inkColor, damageMobs, new EntityDamageSource(Splatcraft.MOD_ID + "." + damageSourceId, source) {});
    }
    public static void attackDamage(World world, LivingEntity target, float damage, InkColor inkColor, boolean damageMobs, DamageSource damageSource) {
        InkColor targetColor = ColorUtils.getEntityColor(target);

        if ( damage != 0 && (
            ( damageMobs || SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.INK_MOB_DAMAGE))
            || (SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.INK_FRIENDLY_FIRE) || !targetColor.matches(inkColor.color))
        )) {
            if (target instanceof InkableEntity && !((InkableEntity) target).onEntityInked(damageSource, damage, inkColor) && !targetColor.equals(InkColors.NONE)) {
                target.damage(damageSource, damage);
            }
        }
    }
}
