package net.splatcraft.entity.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;

public class SplatcraftDamageSource extends DamageSource {
    public static final DamageSource INKED = new SplatcraftDamageSource("inked").setBypassesArmor();
    public static final DamageSource OUT_OF_ARENA = new SplatcraftDamageSource("out_of_arena").setBypassesArmor();

    protected SplatcraftDamageSource(String name) {
        super(new Identifier(Splatcraft.MOD_ID, name).toString());
    }
}
