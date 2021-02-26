package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.entity.ColorableEntity;
import com.cibernet.splatcraft.entity.damage.vanilla.PublicDamageSource;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.Objects;

public class InkDamageUtils {
    public static final DamageSource VOID_DAMAGE = new PublicDamageSource("outOfWorld").setBypassesArmor();
    public static final DamageSource ENEMY_INK = new PublicDamageSource("enemyInk").setBypassesArmor();
    public static final DamageSource WATER = new PublicDamageSource("water").setBypassesArmor();

    static {
        VOID_DAMAGE.bypassesArmor();
    }

    public static boolean splatDamage(World world, LivingEntity target, float damage, InkColor color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType) {
        return damage(world, target, damage, color, source, sourceItem, damageMobs, inkType, "splat");
    }

    public static boolean rollDamage(World world, LivingEntity target, float damage, InkColor color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType) {
        return damage(world, target, damage, color, source, sourceItem, damageMobs, inkType, "roll");
    }

    public static boolean damage(World world, LivingEntity target, float damage, InkColor color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType, String name) {
        if (damage == 0) {
            return false;
        }

        boolean doDamage = damageMobs || SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.INK_MOB_DAMAGE);
        InkColor targetColor = ColorUtils.getEntityColor(target);

        if (targetColor != InkColors.NONE) {
            doDamage = targetColor != color || SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.INK_FRIENDLY_FIRE);
        }

        InkDamageSource damageSource = new InkDamageSource(name, source, source, sourceItem);
        if (target instanceof ColorableEntity) {
            doDamage = ((ColorableEntity) target).onEntityInked(damageSource, damage, color);
        }
        if (doDamage) {
            target.damage(damageSource, damage);
        }

        return doDamage;
    }

    public static class InkDamageSource extends ProjectileDamageSource {
        private final ItemStack weapon;

        public InkDamageSource(String damageType, Entity source, Entity entity, ItemStack stack) {
            super(damageType, source, entity);
            this.weapon = stack;
        }

        @Override
        public Text getDeathMessage(LivingEntity entity) {
            Text text;
            if (this.getSource() == null) {
                text = Objects.requireNonNull(this.source).getDisplayName();
            } else {
                text = this.getSource().getDisplayName();
            }

            String s = "death.attack." + this.weapon;
            String s1 = s + ".item";
            return !weapon.isEmpty()
                ? new TranslatableText(s1, entity.getDisplayName(), text, weapon.toHoverableText())
                : new TranslatableText(s, entity.getDisplayName(), text);
        }
    }
}
