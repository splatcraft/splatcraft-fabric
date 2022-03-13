package net.splatcraft.api.entity.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;

public class SplatcraftDamageSource extends DamageSource {
    public static final String ID_INKED = name("inked");

    public static final DamageSource INKED_ENVIRONMENT = new SplatcraftDamageSource("inked_environment").setBypassesArmor();
    public static final DamageSource INK_IN_WATER = new SplatcraftDamageSource("ink_in_water").setBypassesArmor();
    public static final DamageSource OUT_OF_ARENA = new SplatcraftDamageSource("out_of_arena").setBypassesArmor();

    public static DamageSource inked(LivingEntity attacker) {
        return new EntityDamageSource(ID_INKED, attacker);
    }

    protected SplatcraftDamageSource(String name) {
        super(name(name));
    }

    private static String name(String name) {
        return new Identifier(Splatcraft.MOD_ID, name).toString();
    }
}
