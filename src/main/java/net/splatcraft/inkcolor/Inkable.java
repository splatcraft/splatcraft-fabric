package net.splatcraft.inkcolor;

import net.minecraft.text.Text;

public interface Inkable {
    InkColor getInkColor();
    boolean setInkColor(InkColor inkColor);

    default boolean hasInkColor() {
        return true;
    }

    Text getTextForCommand();
}
