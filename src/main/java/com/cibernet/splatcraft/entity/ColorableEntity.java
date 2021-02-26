package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkDamageUtils;

public interface ColorableEntity {
    InkColor getColor();
    void setColor(InkColor color);

    default boolean onEntityInked(InkDamageUtils.InkDamageSource source, float damage, InkColor color) {
        return false;
    }
}
