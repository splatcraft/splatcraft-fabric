package net.splatcraft.entity.access;

import net.splatcraft.entity.PackedInput;

public interface InputPlayerEntityAccess {
    PackedInput getPackedInput();
    void setPackedInput(PackedInput input);
}
