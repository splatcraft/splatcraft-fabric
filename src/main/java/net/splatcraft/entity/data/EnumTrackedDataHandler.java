package net.splatcraft.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;

public record EnumTrackedDataHandler<T extends Enum<T>>(Class<T> clazz) implements TrackedDataHandler<T> {
    @Override
    public void write(PacketByteBuf buf, T value) {
        buf.writeEnumConstant(value);
    }

    @Override
    public T read(PacketByteBuf buf) {
        return buf.readEnumConstant(this.clazz);
    }

    @Override
    public T copy(T value) {
        return value;
    }
}
