package net.splatcraft.entity;

import net.minecraft.util.math.Vec3d;

public interface InputPlayerEntityAccess {
    Vec3d getInputSpeeds();
    void setInputSpeeds(Vec3d inputSpeeds);

    default boolean isSidewaysPressed() {
        return this.getInputSpeeds().x != 0;
    }

    default boolean isForwardPressed() {
        return this.getInputSpeeds().z > 0;
    }
}
