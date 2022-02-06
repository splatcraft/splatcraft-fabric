package net.splatcraft.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.PackedInput;
import net.splatcraft.entity.access.InputPlayerEntityAccess;

import static net.splatcraft.network.PacketIdentifiers.*;

public class NetworkingCommon {
    static {
        // packet receivers
        ServerPlayNetworking.registerGlobalReceiver(KEY_CHANGE_SQUID_FORM, (server, player, handler, buf, responseSender) -> {
            boolean squid = buf.readBoolean();

            PlayerDataComponent data = PlayerDataComponent.get(player);
            if (data.setSquid(squid)) {
                PacketByteBuf returnBuf = PacketByteBufs.create();
                returnBuf.writeBoolean(squid);
                ServerPlayNetworking.send(player, KEY_CHANGE_SQUID_FORM_RESPONSE, returnBuf);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(CLIENT_INPUT, (server, player, handler, buf, responseSender) -> {
            ((InputPlayerEntityAccess) player).setPackedInput(PackedInput.fromPacket(buf));
        });
    }

    public static void s2cInit(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, S2C_INIT, PacketByteBufs.empty());
    }
}
