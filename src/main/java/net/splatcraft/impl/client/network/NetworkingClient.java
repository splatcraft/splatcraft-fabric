package net.splatcraft.impl.client.network;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.splatcraft.api.component.PlayerDataComponent;
import net.splatcraft.impl.client.config.ClientConfig;
import net.splatcraft.impl.entity.PackedInput;

import static net.splatcraft.impl.network.PacketIdentifiers.*;

@Environment(EnvType.CLIENT)
public final class NetworkingClient implements ClientModInitializer {
    private static boolean presentOnServer = false;

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            presentOnServer = false;
        });

        // packet receivers
        ClientPlayNetworking.registerGlobalReceiver(KEY_CHANGE_SQUID_FORM_RESPONSE, (client, handler, buf, responseSender) -> {
            boolean squid = buf.readBoolean();
            PlayerDataComponent data = PlayerDataComponent.get(client.player);
            data.setSquid(squid);
        });

        ClientPlayNetworking.registerGlobalReceiver(S2C_INIT, (client, handler, buf, responseSender) -> presentOnServer = true);
    }

    public static boolean isSplatcraftPresentOnServer() {
        return presentOnServer;
    }

    public static void keyChangeSquidForm(boolean squid) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(squid);
        ClientPlayNetworking.send(KEY_CHANGE_SQUID_FORM, buf);

        // if configured, instantly change squid form client-side without response
        if (!ClientConfig.INSTANCE.optimiseDesync.getValue()) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerDataComponent data = PlayerDataComponent.get(client.player);
            data.setSquid(squid);
        }
    }

    public static void clientInput(PackedInput input) {
        ClientPlayNetworking.send(CLIENT_INPUT, input.toPacket());
    }
}
