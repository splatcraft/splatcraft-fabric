package com.cibernet.splatcraft.inkcolor;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.init.SplatcraftRegistries;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;

@SuppressWarnings("unused")
public class InkColors {
    private static HashMap<Identifier, InkColor> ALL = new LinkedHashMap<>();
    private static HashMap<Identifier, InkColor> CACHED_DATA = new LinkedHashMap<>();

    public static final InkColor NONE = register("none", ColorUtil.DEFAULT);

    // starter Colors
    public static final InkColor ORANGE = register("orange", 0xDF641A);
    public static final InkColor BLUE = register("blue", 0x26229F);
    public static final InkColor PINK = register("pink", 0xC83D79);
    public static final InkColor GREEN = register("green", 0x409D3B);

    // basic colors
    public static final InkColor LIGHT_BLUE = register("light_blue", 0x228cff);
    public static final InkColor TURQUOISE = register("turquoise", 0x048188);
    public static final InkColor YELLOW = register("yellow", 0xe1a307);
    public static final InkColor LILAC = register("lilac", 0x4d24a3);
    public static final InkColor LEMON = register("lemon", 0x91b00b);
    public static final InkColor PLUM = register("plum", 0x830b9c);

    // pastel colors
    public static final InkColor CYAN = register("cyan", 0x4ACBCB);
    public static final InkColor PEACH = register("peach", 0xEA8546);
    public static final InkColor MINT = register("mint", 0x08B672);
    public static final InkColor CHERRY = register("cherry", 0xE24F65);

    // neon colors
    public static final InkColor NEON_PINK = register("neon_pink", 0xcf0466);
    public static final InkColor NEON_GREEN = register("neon_green", 0x17a80d);
    public static final InkColor NEON_ORANGE = register("neon_orange", 0xe85407);
    public static final InkColor NEON_BLUE = register("neon_blue", 0x2e0cb5);

    // hero Colors
    public static final InkColor HERO_YELLOW = register("hero_yellow", 0xBDDD00);
    public static final InkColor OCTO_PINK = register("octo_pink", 0xE51B5E);

    // special Colors
    public static final InkColor MOJANG = register("mojang", 0xDF242F);
    public static final InkColor COBALT = register("cobalt", 0x005682);
    public static final InkColor ICE = register("ice", 0x88ffc1);
    public static final InkColor FLORAL = register("floral", 0xFF9BEE);

    // vanilla colors
    public static final InkColor DYE_WHITE = register(DyeColor.WHITE);
    public static final InkColor DYE_ORANGE = register(DyeColor.ORANGE);
    public static final InkColor DYE_MAGENTA = register(DyeColor.MAGENTA);
    public static final InkColor DYE_LIGHT_BLUE = register(DyeColor.LIGHT_BLUE);
    public static final InkColor DYE_YELLOW = register(DyeColor.YELLOW);
    public static final InkColor DYE_LIME = register(DyeColor.LIME);
    public static final InkColor DYE_PINK = register(DyeColor.PINK);
    public static final InkColor DYE_GRAY = register(DyeColor.GRAY);
    public static final InkColor DYE_LIGHT_GRAY = register(DyeColor.LIGHT_GRAY);
    public static final InkColor DYE_CYAN = register(DyeColor.CYAN);
    public static final InkColor DYE_PURPLE = register(DyeColor.PURPLE);
    public static final InkColor DYE_BLUE = register(DyeColor.BLUE);
    public static final InkColor DYE_BROWN = register(DyeColor.BROWN);
    public static final InkColor DYE_GREEN = register(DyeColor.GREEN);
    public static final InkColor DYE_RED = register(DyeColor.RED);
    public static final InkColor DYE_BLACK = register(DyeColor.BLACK);

    private static InkColor register(Identifier id, InkColor inkColor) {
        return Registry.register(SplatcraftRegistries.INK_COLORS, id, inkColor);
    }
    private static InkColor register(String id, InkColor inkColor) {
        return register(new Identifier(Splatcraft.MOD_ID, id), inkColor);
    }
    private static InkColor register(String id, int color) {
        return register(id, new InkColor(new Identifier(Splatcraft.MOD_ID, id), color));
    }
    private static InkColor register(DyeColor dyeColor) {
        return register(new Identifier(dyeColor.getName()), new InkColor(dyeColor));
    }

    public static Optional<InkColor> getOrEmpty(Identifier id) {
        return Optional.ofNullable(get(id));
    }
    @Nullable
    public static InkColor get(Identifier id) {
        return getAll().getOrDefault(id, null);
    }
    @NotNull
    public static InkColor getNonNull(Identifier id) {
        InkColor inkColor = InkColors.get(id);
        return inkColor == null ? InkColors.NONE : inkColor;
    }

    public static HashMap<Identifier, InkColor> getAll() {
        return InkColors.ALL;
    }
    public static void setAll(HashMap<Identifier, InkColor> all) {
        InkColors.ALL = all;
    }

    public static HashMap<Identifier, InkColor> getCachedData() {
        return InkColors.CACHED_DATA;
    }

    public static void rebuildIfNeeded(HashMap<Identifier, InkColor> inputData) {
        if (ALL.isEmpty() || inputData.isEmpty() || !inputData.equals(CACHED_DATA)) {
            HashMap<Identifier, InkColor> all = new LinkedHashMap<>();

            SplatcraftRegistries.INK_COLORS.forEach(inkColor -> all.put(inkColor.id, inkColor));
            inputData.forEach(all::put);

            ALL = all;
            CACHED_DATA = inputData;
        }
    }

    public static void sync(ServerPlayNetworkHandler handler, @Nullable PacketSender player, @Nullable MinecraftServer server) {
        InkColors.sync(handler.player);
        Splatcraft.log("Synchronised ink colors with " + handler.player.getEntityName());
    }
    public static void sync(Collection<ServerPlayerEntity> playerLookup) {
        for (ServerPlayerEntity player : playerLookup) {
            InkColors.sync(player);
        }
        Splatcraft.log("Synchronised ink colors with " + playerLookup.size() + " players");
    }
    public static void sync(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();

        CompoundTag tag = new CompoundTag();
        ListTag inkColors = new ListTag();
        InkColors.getAll().forEach((identifier, inkColor) -> inkColors.add(inkColor.toTag()));
        tag.put("InkColors", inkColors);
        buf.writeCompoundTag(tag);

        ServerPlayNetworking.send(player, SplatcraftNetworkingConstants.SYNC_INK_COLORS_REGISTRY_PACKET_ID, buf);
    }
}
