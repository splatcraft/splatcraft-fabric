package net.splatcraft.entity;

import net.splatcraft.inkcolor.InkColor;

public interface Inkable {
    InkColor getInkColor();
    boolean setInkColor(InkColor inkColor);
}
