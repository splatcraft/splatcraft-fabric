package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.signal.SignalWhitelistManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class SplatcraftNetworking {
    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(
            SplatcraftNetworkingConstants.SET_SQUID_FORM_PACKET_ID,
            (server, player, handler, buf, responseSender) -> {
                boolean isSquid = buf.readBoolean();
                server.execute(() -> LazyPlayerDataComponent.setIsSquid(player, isSquid));
            }
        );
        ServerPlayNetworking.registerGlobalReceiver(
            SplatcraftNetworkingConstants.PLAY_PLAYER_SIGNAL_PACKET_ID,
            (server, player, handler, buf, responseSender) -> {
                UUID uuid = buf.readUuid();
                Identifier id = buf.readIdentifier();

                PacketByteBuf forwardBuf = PacketByteBufs.create();
                forwardBuf.writeUuid(uuid);
                forwardBuf.writeIdentifier(id);

                server.execute(
                    () -> {
                        if (SignalWhitelistManager.isAllowed(id)) {
                            for (ServerPlayerEntity serverPlayer : PlayerLookup.tracking(player.getServerWorld(), player.getBlockPos())) {
                                ServerPlayNetworking.send(serverPlayer, SplatcraftNetworkingConstants.PLAY_PLAYER_SIGNAL_PACKET_ID, forwardBuf);
                            }
                        } else {
                            ServerPlayNetworking.send(player, SplatcraftNetworkingConstants.DISALLOWED_PLAYER_SIGNAL_PACKET_ID, forwardBuf);
                        }
                    }
                );
            }
        );
    }

    public static void sendPlayInkDeathEffects(Entity entity) {
        if (entity.world != null && !entity.world.isClient && !entity.isSpectator()) {
            MinecraftServer server = entity.world.getServer();
            if (server != null) {
                for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(entity.getId());

                    ServerPlayNetworking.send(serverPlayer, SplatcraftNetworkingConstants.PLAY_INK_DEATH_EFFECTS, buf);
                }
            }
        }
    }
}
