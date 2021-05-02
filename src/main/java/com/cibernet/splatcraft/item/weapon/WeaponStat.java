package com.cibernet.splatcraft.item.weapon;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.text.BaseText;
import net.minecraft.text.TranslatableText;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class WeaponStat {
    private final String name;
    private final Object value;

    public WeaponStat(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public BaseText getTextComponent() {
        Object arg = this.value;

        if (arg instanceof Number) {
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            arg = df.format(arg);
        }

        return new TranslatableText(Splatcraft.MOD_ID + ".weaponStat." + name, arg);
    }
}
