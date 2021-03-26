package com.cibernet.splatcraft.item.weapon;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.item.ItemStack;
import net.minecraft.text.BaseText;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WeaponStat {
    private final String name;
    private final StatValueGetter valueGetter;

    public WeaponStat(String name, StatValueGetter valueGetter) {
        this.name = name;
        this.valueGetter = valueGetter;
    }

    public int getStatValue(ItemStack stack, @Nullable World world) {
        return valueGetter.get(stack, world);
    }

    public BaseText getTextComponent(ItemStack stack, World world) {
        return new TranslatableText(Splatcraft.MOD_ID + ".weaponStat." + name, getStatValue(stack, world));
    }

    public interface StatValueGetter {
        int get(ItemStack stack, @Nullable World world);
    }
}
