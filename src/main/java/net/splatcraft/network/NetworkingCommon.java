package net.splatcraft.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.splatcraft.component.PlayerDataComponent;

public class NetworkingCommon {
    static {
        ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.KEY_CHANGE_SQUID_FORM, (server, player, handler, buf, responseSender) -> {
            boolean squid = buf.readBoolean();

            PlayerDataComponent data = PlayerDataComponent.get(player);
            if (data.setSquid(squid)) {
                PacketByteBuf returnBuf = PacketByteBufs.create();
                returnBuf.writeBoolean(squid);
                ServerPlayNetworking.send(player, PacketIdentifiers.KEY_CHANGE_SQUID_FORM_RESPONSE, returnBuf);
            }
        });
    }
}
