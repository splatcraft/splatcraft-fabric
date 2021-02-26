package com.cibernet.splatcraft.entity.damage.vanilla;

import net.minecraft.entity.damage.DamageSource;

public class PublicDamageSource extends DamageSource {
    public PublicDamageSource(String name) {
        super(name);
    }

    @Override
    public DamageSource setBypassesArmor() {
        return super.setBypassesArmor();
    }
}
