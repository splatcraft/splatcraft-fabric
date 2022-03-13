package net.splatcraft.impl.client.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ClientPlayerEntityAccess {
    float getInkOverlayTick();
    int getInkOverlayColor();
}
