package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class InkColor {
    private final int color;
    private final Identifier id;

    public InkColor(Identifier id, int color) {
        this.id = id;
        this.color = color;
    }
    public InkColor(String id, int color) {
        this(new Identifier(Splatcraft.MOD_ID, id), color);
    }
    public InkColor(DyeColor dyeColor) {
        this(new Identifier(dyeColor.getName()), dyeColor.color);
    }

    public String getTranslationKey() {
        return Splatcraft.MOD_ID + ".ink_color." + this.id;
    }

    public static InkColor getFromId(Identifier identifier) {
        InkColor object = InkColors.get(identifier);
        return object == null ? InkColors.NONE : object;
    }
    public static InkColor getFromId(String identifier) {
        return InkColor.getFromId(Identifier.tryParse(identifier));
    }

    public int getColor() {
        return this.color;
    }
    @Environment(EnvType.CLIENT)
    public int getColorOrLocked() {
        return this != InkColors.NONE && SplatcraftConfig.INK.colorLock.value
            ? (this.matches(PlayerDataComponent.getInkColor(MinecraftClient.getInstance().player).getColor())
                    ? InkColors.COLOR_LOCK_FRIENDLY
                    : InkColors.COLOR_LOCK_HOSTILE
                ).getColor()
            : this.color;
    }
    public Identifier getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InkColor inkColor = (InkColor) o;
        return color == inkColor.color && Objects.equals(id, inkColor.id);
    }

    /**
     * A hard check for ink color matching.
     */
    public boolean matches(InkColor color) {
        return this == color;
    }

    /**
     * A soft check for ink color matching. Checks only the color's id.
     */
    public boolean matches(int color) {
        return this.getColor() == color;
    }
}
