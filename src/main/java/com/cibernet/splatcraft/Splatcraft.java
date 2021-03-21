package com.cibernet.splatcraft;

import com.cibernet.splatcraft.command.InkColorCommand;
import com.cibernet.splatcraft.command.argument.InkColorArgumentType;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.handler.PlayerHandler;
import com.cibernet.splatcraft.init.*;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import com.cibernet.splatcraft.tag.SplatcraftBlockTags;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.andante.chord.client.gui.itemgroup.AbstractTabbedItemGroup;
import me.andante.chord.client.gui.itemgroup.ItemGroupTab;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Splatcraft implements ModInitializer {
    public static final String MOD_ID = "splatcraft";
    public static final String MOD_NAME = "Splatcraft";

    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final ItemGroup ITEM_GROUP = new AbstractTabbedItemGroup(Splatcraft.MOD_ID) {
        @Override
        protected List<ItemGroupTab> initTabs() {
            return ImmutableList.of(
                createTab(SplatcraftBlocks.INKWELL, "colorables"),
                createTab(SplatcraftItems.SPLAT_ROLLER, "weapons"),
                createTab(SplatcraftBlocks.GRATE, "stage_tools")
            );
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(SplatcraftBlocks.CANVAS);
        }
    };

    @Override
    public void onInitialize() {
        log("Initializing");

        new SplatcraftRegistries();

        new SplatcraftBlockTags();
        // new SplatcraftInkColorTags();

        new SplatcraftAttributes();
        new SplatcraftGameRules();
        new SplatcraftSoundEvents();

        new SplatcraftBlockEntities();
        new SplatcraftBlocks();
        new SplatcraftItems();
        new SplatcraftEntities();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(Splatcraft.MOD_ID, "ink_colors");
            }

            @Override
            public void apply(ResourceManager manager) {
                Collection<Identifier> inkColors = manager.findResources(Splatcraft.MOD_ID + "_ink_colors", (r) -> r.endsWith(".json") || r.endsWith(".json5"));
                HashMap<Identifier, InkColor> loaded = new LinkedHashMap<>();

                for (Identifier fileId : inkColors) {
                    try (
                        InputStream is = manager.getResource(fileId).getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is))
                    ) {
                        JsonObject inkColorData = new JsonParser().parse(reader).getAsJsonObject();
                        JsonElement colorElement = inkColorData.get("color");
                        int color = colorElement.getAsJsonPrimitive().isString()
                            ? Integer.parseInt(colorElement.getAsString().replaceFirst("#", ""), 16)
                            : colorElement.getAsInt();

                        String id1 = fileId.toString();
                        String id2 = id1.substring(id1.indexOf("/") + 1);
                        InkColor inkColor = new InkColor(new Identifier(fileId.getNamespace(), id2.substring(0, id2.lastIndexOf("."))), color);

                        loaded.put(inkColor.getId(), inkColor);
                    } catch (Exception e) {
                        log(Level.ERROR, "Unable to load ink color from '" + fileId + "'.");
                        e.printStackTrace();
                    }
                }

                InkColors.rebuildIfNeeded(loaded);
                log("Loaded " + InkColors.getAll().size() + " ink colors!");
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.PLAYER_TOGGLE_SQUID_PACKET_ID, (server, player, handler, buf, responseSender) -> server.execute(() -> PlayerDataComponent.toggleSquidForm(player)));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PacketByteBuf buf = PacketByteBufs.create();

            CompoundTag tag = new CompoundTag();
            ListTag inkColors = new ListTag();
            InkColors.getAll().forEach((identifier, inkColor) -> inkColors.add(inkColor.toTag()));
            tag.put("InkColors", inkColors);
            buf.writeCompoundTag(tag);

            sender.sendPacket(SplatcraftNetworkingConstants.SYNC_INK_COLORS_REGISTRY_PACKET_ID, buf);
            Splatcraft.log("Synchronised ink colors with " + handler.player.getDisplayName().asString());
        });

        PlayerHandler.registerEvents();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> InkColorCommand.register(dispatcher));
        ArgumentTypes.register(new Identifier(Splatcraft.MOD_ID, "ink_color").toString(), InkColorArgumentType.class, new ConstantArgumentSerializer<>(InkColorArgumentType::inkColor));

        log("Initialized");
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static Identifier texture(String path) {
        return new Identifier(MOD_ID, "textures/" + path + ".png");
    }
}
