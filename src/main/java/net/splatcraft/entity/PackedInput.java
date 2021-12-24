package net.splatcraft.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.input.Input;
import net.minecraft.network.PacketByteBuf;

public record PackedInput(boolean forward, boolean back, boolean left, boolean right, boolean jumping, boolean sneaking) {
    public static final PackedInput EMPTY = new PackedInput(false, false, false, false, false, false);

    @Environment(EnvType.CLIENT)
    public static PackedInput of(Input input) {
        return new PackedInput(input.pressingForward, input.pressingBack, input.pressingLeft, input.pressingRight, input.jumping, input.sneaking);
    }

    public static PackedInput fromPacket(PacketByteBuf buf) {
        return new PackedInput(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

    public PacketByteBuf toPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(this.forward());
        buf.writeBoolean(this.back());
        buf.writeBoolean(this.left());
        buf.writeBoolean(this.right());
        buf.writeBoolean(this.jumping());
        buf.writeBoolean(this.sneaking());
        return buf;
    }

    public boolean sideways() {
        return this.left() || this.right();
    }
}
