package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

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
    public Identifier getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.getId().toString();
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
