package net.splatcraft.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.splatcraft.client.network.NetworkingClient;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.PackedInput;
import net.splatcraft.entity.access.InputPlayerEntityAccess;

import static net.splatcraft.network.PacketIdentifiers.*;
import static net.splatcraft.world.SplatcraftGameRules.*;

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

    public static void updateUniversalInk(ServerPlayerEntity player, GameRules.BooleanRule rule) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(rule.get());
        ServerPlayNetworking.send(player, UPDATE_UNIVERSAL_INK, buf);
    }

    public static void updateEnemyInkSlowness(ServerPlayerEntity player, GameRules.BooleanRule rule) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(rule.get());
        ServerPlayNetworking.send(player, UPDATE_ENEMY_INK_SLOWNESS, buf);
    }

    public static boolean universalInk(World world) {
        return world.isClient ? clientUniversalInk() : gameRule(world, UNIVERSAL_INK);
    }

    @Environment(EnvType.CLIENT)
    private static boolean clientUniversalInk() {
        return NetworkingClient.universalInk();
    }

    public static boolean enemyInkSlowness(World world) {
        return world.isClient ? clientEnemyInkSlowness() : gameRule(world, ENEMY_INK_SLOWNESS);
    }

    @Environment(EnvType.CLIENT)
    private static boolean clientEnemyInkSlowness() {
        return NetworkingClient.enemyInkSlowness();
    }
}
