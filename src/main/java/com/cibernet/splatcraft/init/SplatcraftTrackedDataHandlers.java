package com.cibernet.splatcraft.init;

import com.cibernet.splatcraft.entity.enums.SquidBumperDisplayType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;

public class SplatcraftTrackedDataHandlers {
    public static final TrackedDataHandler<SquidBumperDisplayType> SQUID_BUMPER_DISPLAY_TYPE = new TrackedDataHandler<SquidBumperDisplayType>() {
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
    };

    public SplatcraftTrackedDataHandlers() {
        TrackedDataHandlerRegistry.field_13328.add(SQUID_BUMPER_DISPLAY_TYPE);
    }
}
