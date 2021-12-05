package net.splatcraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.PacketByteBuf;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.mixin.client.AbstractClientPlayerEntityInvoker;
import net.splatcraft.network.PacketIdentifiers;

@Environment(EnvType.CLIENT)
public class NetworkingClient {
    static {
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.KEY_CHANGE_SQUID_FORM_RESPONSE, (client, handler, buf, responseSender) -> {
            boolean squid = buf.readBoolean();

            PlayerDataComponent data = PlayerDataComponent.get(client.player);
            data.setSquid(squid);
        });
    }

    public static void sendKeyChangeSquidForm(boolean squid) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(squid);
        ClientPlayNetworking.send(PacketIdentifiers.KEY_CHANGE_SQUID_FORM, buf);

        // if configured, instantly change squid form client-side without response
        int latencyForInstantSquidFormChange = ClientConfig.INSTANCE.latencyForInstantSquidFormChange.getValue();
        if (latencyForInstantSquidFormChange > 1) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player instanceof AbstractClientPlayerEntityInvoker invoker) {
                PlayerListEntry entry = invoker.invoke_getPlayerListEntry();
                if (entry.getLatency() >= latencyForInstantSquidFormChange) {
                    PlayerDataComponent data = PlayerDataComponent.get(client.player);
                    data.setSquid(squid);
                }
            }
        }
    }
}
