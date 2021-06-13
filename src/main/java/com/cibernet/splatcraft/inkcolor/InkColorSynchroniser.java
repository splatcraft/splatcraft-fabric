package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.init.SplatcraftRegistries;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class InkColorSynchroniser {
    static HashMap<Identifier, InkColor> CACHED_DATA = new LinkedHashMap<>();

    public static HashMap<Identifier, InkColor> getCachedData() {
        return InkColorSynchroniser.CACHED_DATA;
    }

    public static void rebuildIfNeeded(HashMap<Identifier, InkColor> inputData) {
        if (InkColors.getAll().isEmpty() || inputData.isEmpty() || !inputData.equals(InkColorSynchroniser.CACHED_DATA)) {
            HashMap<Identifier, InkColor> all = new LinkedHashMap<>();

            SplatcraftRegistries.INK_COLORS.forEach(inkColor -> all.put(inkColor.id, inkColor));
            inputData.forEach(all::put);

            InkColors.setAll(all);
            InkColorSynchroniser.CACHED_DATA = inputData;
        }
    }

    public static void sync(ServerPlayNetworkHandler handler, @Nullable PacketSender player, @Nullable MinecraftServer server) {
        InkColorSynchroniser.sync(handler.player);
        Splatcraft.log("Synchronised ink colors with " + handler.player.getEntityName());
    }

    public static void sync(Collection<ServerPlayerEntity> playerLookup) {
        for (ServerPlayerEntity player : playerLookup) {
            InkColorSynchroniser.sync(player);
        }
        Splatcraft.log("Synchronised ink colors with " + playerLookup.size() + " players");
    }

    public static void sync(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();

        NbtList inkColors = new NbtList();
        InkColors.getAll().forEach((identifier, inkColor) -> inkColors.add(inkColor.writeNbt()));

        NbtCompound tag = new NbtCompound();
        tag.put("InkColors", inkColors);

        buf.writeNbt(tag);

        ServerPlayNetworking.send(player, SplatcraftNetworkingConstants.SYNC_INK_COLORS_REGISTRY_PACKET_ID, buf);
    }
}
