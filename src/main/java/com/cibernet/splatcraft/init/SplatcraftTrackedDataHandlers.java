package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.entity.enums.SquidBumperDisplayType;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;

public class SplatcraftTrackedDataHandlers {
    private static final HashMap<Integer, TrackedDataHandler<?>> HANDLERS = new HashMap<>();
    private static int prevId = 0;

    public static final TrackedDataHandler<InkColor> INK_COLOR = register(new TrackedDataHandler<InkColor>() {
        @Override
        public void write(PacketByteBuf packetByteBuf, InkColor inkColor) {
            packetByteBuf.writeString(inkColor.toString());
        }

        @Override
        public InkColor read(PacketByteBuf packetByteBuf) {
            return InkColor.fromNonNull(packetByteBuf.readString());
        }

        @Override
        public InkColor copy(InkColor inkColor) {
            return inkColor;
        }
    });
    public static final TrackedDataHandler<InkType> INK_TYPE = register(new TrackedDataHandler<InkType>() {
        @Override
        public void write(PacketByteBuf packetByteBuf, InkType inkType) {
            packetByteBuf.writeString(inkType.toString());
        }

        @Override
        public InkType read(PacketByteBuf packetByteBuf) {
            String inkType = packetByteBuf.readString();
            return inkType.isEmpty() ? InkType.NORMAL : InkType.valueOf(inkType);
        }

        @Override
        public InkType copy(InkType inkColor) {
            return inkColor;
        }
    });
    public static final TrackedDataHandler<SquidBumperDisplayType> SQUID_BUMPER_DISPLAY_TYPE = register(new TrackedDataHandler<SquidBumperDisplayType>() {
        @Override
        public void write(PacketByteBuf packetByteBuf, SquidBumperDisplayType displayType) {
            packetByteBuf.writeEnumConstant(displayType);
        }

        @Override
        public SquidBumperDisplayType read(PacketByteBuf packetByteBuf) {
            return packetByteBuf.readEnumConstant(SquidBumperDisplayType.class);
        }

        @Override
        public SquidBumperDisplayType copy(SquidBumperDisplayType displayType) {
            return displayType;
        }
    });

    static {
        HANDLERS.forEach((id, trackedDataHandler) -> TrackedDataHandlerRegistry.field_13328.put(trackedDataHandler, id));
    }

    public static <T> TrackedDataHandler<T> register(TrackedDataHandler<T> trackedDataHandler) {
        HANDLERS.put(317814 + prevId++, trackedDataHandler);
        return trackedDataHandler;
    }
}
