package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class SplatcraftNetworking {
    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.PLAYER_TOGGLE_SQUID_PACKET_ID, (server, player, handler, buf, responseSender) -> server.execute(() -> PlayerDataComponent.toggleSquidForm(player)));
    }
}
