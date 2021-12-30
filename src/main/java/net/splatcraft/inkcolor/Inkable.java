package net.splatcraft.inkcolor;

import net.minecraft.text.Text;

public interface Inkable {
    InkColor getInkColor();
    boolean setInkColor(InkColor inkColor);

    default boolean hasInkColor() {
        return true;
    }

    InkType getInkType();
    boolean setInkType(InkType inkType);

    default boolean hasInkType() {
        return true;
    }

    default void copyInkableTo(Inkable inkable) {
        inkable.setInkColor(this.getInkColor());
        inkable.setInkType(this.getInkType());
    }

    Text getTextForCommand();
}
