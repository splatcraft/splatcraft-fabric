package net.splatcraft.impl.entity.access;

import net.splatcraft.impl.entity.PackedInput;

public interface InputPlayerEntityAccess {
    PackedInput getPackedInput();
    void setPackedInput(PackedInput input);
}
