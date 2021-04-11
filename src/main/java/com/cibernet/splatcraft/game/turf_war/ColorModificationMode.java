package com.cibernet.splatcraft.game.turf_war;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum ColorModificationMode {
    ALL,
    WHITELIST_PLAYER,
    BLACKLIST_PLAYER;

    public String toSnakeCase() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    public ColorModificationMode from(String from) {
        return ColorModificationMode.valueOf(from);
    }
    public ColorModificationMode cycle() {
        List<ColorModificationMode> values = Arrays.stream(ColorModificationMode.values()).collect(Collectors.toList());
        int index = values.indexOf(this);
        return values.get(index >= values.size() - 1 ? 0 : index + 1);
    }
    public static ColorModificationMode getDefault() {
        return ColorModificationMode.ALL;
    }

    public TranslatableText getText() {
        return new TranslatableText("text." + Splatcraft.MOD_ID + ".mode", new TranslatableText("text." + Splatcraft.MOD_ID + ".color_modification_mode." + this.toSnakeCase()));
    }
}
