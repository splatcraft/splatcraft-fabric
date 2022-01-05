package net.splatcraft.entity.access;

import net.minecraft.entity.Entity;
import net.splatcraft.inkcolor.Inkable;

public interface InkableCaster {
    /**
     * Casts this class to an instance of {@link Entity} & {@link Inkable}.
     */
    <T extends Entity & Inkable> T toInkable();
}
