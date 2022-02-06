package net.splatcraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.PackedInput;

import static net.splatcraft.network.PacketIdentifiers.*;

@Environment(EnvType.CLIENT)
public class NetworkingClient {
    private static boolean presentOnServer = false;

    private static boolean leaveSquidFormOnEnemyInk = true;
    private static boolean universalInk = false;
    private static boolean enemyInkSlowness = true;

    static {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            presentOnServer = false;

            leaveSquidFormOnEnemyInk = true;
            universalInk = false;
            enemyInkSlowness = true;
        });

        // packet receivers
        ClientPlayNetworking.registerGlobalReceiver(KEY_CHANGE_SQUID_FORM_RESPONSE, (client, handler, buf, responseSender) -> {
            boolean squid = buf.readBoolean();
            PlayerDataComponent data = PlayerDataComponent.get(client.player);
            data.setSquid(squid);
        });

        ClientPlayNetworking.registerGlobalReceiver(S2C_INIT, (client, handler, buf, responseSender) -> presentOnServer = true);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_LEAVE_SQUID_FORM_ON_ENEMY_INK, (client, handler, buf, responseSender) -> leaveSquidFormOnEnemyInk = buf.readBoolean());
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_UNIVERSAL_INK, (client, handler, buf, responseSender) -> universalInk = buf.readBoolean());
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_ENEMY_INK_SLOWNESS, (client, handler, buf, responseSender) -> enemyInkSlowness = buf.readBoolean());
    }

    public static boolean isSplatcraftPresentOnServer() {
        return presentOnServer;
    }

    public static boolean leaveSquidFormOnEnemyInk() {
        return leaveSquidFormOnEnemyInk;
    }

    public static boolean universalInk() {
        return universalInk;
    }

    public static boolean enemyInkSlowness() {
        return enemyInkSlowness;
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
