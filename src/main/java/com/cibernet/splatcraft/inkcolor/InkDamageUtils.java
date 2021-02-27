package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.entity.InkableEntity;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.Objects;

@SuppressWarnings("unused")
public class InkDamageUtils {
    public static boolean splatDamage(World world, LivingEntity target, float damage, InkColor color, Entity source, ItemStack sourceItem, boolean damageMobs) {
        return InkDamageUtils.damage(world, target, damage, color, source, sourceItem, damageMobs, "splat");
    }

    public static boolean rollDamage(World world, LivingEntity target, float damage, InkColor color, Entity source, ItemStack sourceItem, boolean damageMobs) {
        return InkDamageUtils.damage(world, target, damage, color, source, sourceItem, damageMobs, "roll");
    }

    public static boolean damage(World world, LivingEntity target, float damage, InkColor color, Entity source, ItemStack sourceItem, boolean damageMobs, String id) {
        InkDamageSource damageSource = new InkDamageSource(id, source, source, sourceItem);
        InkColor targetColor = ColorUtils.getEntityColor(target);

        if ( damage != 0 && (
            damageMobs || SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.INK_MOB_DAMAGE)
                || targetColor != InkColors.NONE && targetColor != color || SplatcraftGameRules.getBoolean(world, SplatcraftGameRules.INK_FRIENDLY_FIRE)
                || (target instanceof InkableEntity && ((InkableEntity) target).onEntityInked(damageSource, damage, color))
        )) {
            target.damage(damageSource, damage);
            return true;
        }

        return false;
    }

    public static class InkDamageSource extends ProjectileDamageSource {
        private final ItemStack stack;

        public InkDamageSource(String damageType, Entity source, Entity entity, ItemStack stack) {
            super(damageType, source, entity);
            this.stack = stack;
        }

        @Override
        public Text getDeathMessage(LivingEntity entity) {
            Text text;
            if (this.getSource() == null) {
                text = Objects.requireNonNull(this.source).getDisplayName();
            } else {
                text = this.getSource().getDisplayName();
            }

            String s = "death.attack." + this.stack;
            String s1 = s + ".item";
            return !this.stack.isEmpty()
                ? new TranslatableText(s1, entity.getDisplayName(), text, this.stack.toHoverableText())
                : new TranslatableText(s, entity.getDisplayName(), text);
        }
    }
}
