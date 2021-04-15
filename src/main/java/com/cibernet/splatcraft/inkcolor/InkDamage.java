package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.entity.InkableEntity;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.util.StringConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.world.World;

public class InkDamage {
    public static boolean splat(World world, LivingEntity target, float damage, InkColor inkColor, Entity source, boolean damageMobs) {
        return InkDamage.attack(world, target, damage, inkColor, source, damageMobs, StringConstants.DAMAGE_SOURCE_SPLAT);
    }

    public static boolean roll(World world, LivingEntity target, float damage, InkColor inkColor, Entity source, boolean damageMobs) {
        return InkDamage.attack(world, target, damage, inkColor, source, damageMobs, StringConstants.DAMAGE_SOURCE_ROLL);
    }

    public static boolean attack(World world, LivingEntity target, float damage, InkColor inkColor, Entity source, boolean damageMobs, String damageSourceId) {
        return InkDamage.attack(world, target, damage, inkColor, damageMobs, new EntityDamageSource(Splatcraft.MOD_ID + "." + damageSourceId, source) {});
    }
    public static boolean attack(World world, LivingEntity target, float damage, InkColor inkColor, boolean damageMobs, DamageSource damageSource) {
        InkColor targetColor = ColorUtils.getEntityColor(target);

        if ( damage != 0 && (
            ( damageMobs || SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.INK_MOB_DAMAGE))
            || (SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.INK_FRIENDLY_FIRE) || !targetColor.matches(inkColor.color))
        )) {
            boolean entityInked = target instanceof InkableEntity && ((InkableEntity) target).onEntityInked(damageSource, damage, inkColor);
            if (!entityInked && !targetColor.equals(InkColors.NONE)) {
                return target.damage(damageSource, damage);
            }

            return entityInked;
        }

        return false;
    }
}
