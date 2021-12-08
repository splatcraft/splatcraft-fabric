package net.splatcraft.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.splatcraft.util.Identifiable;

import java.util.function.Function;

public record IdentifiableTrackedDataHandler<T extends Identifiable>(Function<Identifier, T> reader) implements TrackedDataHandler<T> {
    @Override
    public void write(PacketByteBuf buf, T value) {
        buf.writeIdentifier(value.getId());
    }

    @Override
    public T read(PacketByteBuf buf) {
        return this.reader().apply(buf.readIdentifier());
    }

    @Override
    public T copy(T value) {
        return value;
    }
}
