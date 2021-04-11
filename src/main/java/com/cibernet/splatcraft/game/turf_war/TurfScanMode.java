package com.cibernet.splatcraft.game.turf_war;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum TurfScanMode {
    ALL,
    TOP_DOWN,
    MULTI_LAYERED;

    public String toSnakeCase() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    public TurfScanMode from(String from) {
        return TurfScanMode.valueOf(from);
    }
    public TurfScanMode cycle() {
        List<TurfScanMode> values = Arrays.stream(TurfScanMode.values()).collect(Collectors.toList());
        int index = values.indexOf(this);
        return values.get(index >= values.size() - 1 ? 0 : index + 1);
    }
    public static TurfScanMode getDefault() {
        return TurfScanMode.TOP_DOWN;
    }


    public TranslatableText getText() {
        return new TranslatableText("text." + Splatcraft.MOD_ID + ".mode", new TranslatableText("text." + Splatcraft.MOD_ID + ".scan_mode." + this.toSnakeCase()));
    }
}
