package net.splatcraft.impl.entity.access;

import net.minecraft.entity.Entity;
import net.splatcraft.api.inkcolor.Inkable;

public interface InkableCaster {
    /**
     * Casts this class to an instance of {@link Entity} & {@link Inkable}.
     */
    <T extends Entity & Inkable> T toInkable();
}
