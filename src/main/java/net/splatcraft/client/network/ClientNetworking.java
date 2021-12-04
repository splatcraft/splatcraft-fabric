package net.splatcraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.splatcraft.network.NetworkingCommon;
import net.splatcraft.network.PacketIdentifiers;

@Environment(EnvType.CLIENT)
public class ClientNetworking {
    static {
        ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.SET_SQUID_FORM, (server, player, handler, buf, responseSender) -> {
            boolean squid = buf.readBoolean();
            NetworkingCommon.setSquidForm(player, squid);
        });
    }
}
