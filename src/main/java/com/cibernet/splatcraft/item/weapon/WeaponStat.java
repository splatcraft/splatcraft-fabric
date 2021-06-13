package com.cibernet.splatcraft.item.weapon;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.text.BaseText;
import net.minecraft.text.TranslatableText;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public record WeaponStat(String name, Object value) {
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
