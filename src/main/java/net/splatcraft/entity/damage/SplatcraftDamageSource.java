package net.splatcraft.entity.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

public class SplatcraftDamageSource extends DamageSource {
    public static final DamageSource INKED_ENVIRONMENT = new SplatcraftDamageSource("inked_environment").setBypassesArmor();
    public static final DamageSource OUT_OF_ARENA = new SplatcraftDamageSource("out_of_arena").setBypassesArmor();

    public static DamageSource inked(LivingEntity attacker) {
        return new EntityDamageSource(name("inked"), attacker);
    }

    public SplatcraftDamageSource(String name) {
        super(name(name));
    }

    private static String name(String name) {
        return new Identifier(Splatcraft.MOD_ID, name).toString();
    }
}
