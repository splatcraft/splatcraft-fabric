package com.cibernet.splatcraft;

import com.cibernet.splatcraft.command.ClearInkColorCommand;
import com.cibernet.splatcraft.command.FillInkColorCommand;
import com.cibernet.splatcraft.command.ScanTurfCommand;
import com.cibernet.splatcraft.command.SetInkColorCommand;
import com.cibernet.splatcraft.command.argument.InkColorArgumentType;
import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.handler.PlayerHandler;
import com.cibernet.splatcraft.init.*;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.network.SplatcraftNetworking;
import com.cibernet.splatcraft.signal.SignalWhitelistManager;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Reflection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.andante.chord.item.item_group.AbstractTabbedItemGroup;
import me.andante.chord.item.item_group.ItemGroupTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Splatcraft implements ModInitializer {
    public static final String MOD_ID = "splatcraft";
    public static final String MOD_NAME = "Splatcraft";

    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static MinecraftServer SERVER_INSTANCE = null;

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
        @Environment(EnvType.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(SplatcraftBlocks.CANVAS);
        }

        @Override
        protected ItemGroupTab createTab(ItemStack stack, String id, Tag<Item> tag) {
            return super.createTab(stack, id, tag).setWidgetBackgroundTexture(SplatcraftClient.texture("gui/creative_inventory/item_group/tab_widget"));
        }

        @Override
        @Environment(EnvType.CLIENT)
        public String getTexture() {
            return Splatcraft.MOD_ID + ".png";
        }

        @Override
        public Identifier getIconBackgroundTexture() {
            return SplatcraftClient.texture("gui/creative_inventory_tab");
        }
    };

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitialize() {
        log("Initializing");

        // geckolib
        GeckoLib.initialize();

        // init
        Reflection.initialize(
            SplatcraftRegistries.class,

            InkColors.class,

            SplatcraftStats.class,
            SplatcraftAttributes.class,
            SplatcraftGameRules.class,
            SplatcraftSoundEvents.class,

            SplatcraftBlockEntities.class,
            SplatcraftBlocks.class,
            SplatcraftLoomPatterns.class,
            SplatcraftItems.class,

            SplatcraftEntities.class,
            SplatcraftTrackedDataHandlers.class
        );

        // data
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(Splatcraft.MOD_ID, "ink_colors");
            }

            @Override
            public void apply(ResourceManager manager) {
                Collection<Identifier> inkColors = manager.findResources(Splatcraft.MOD_ID + "/ink_colors", (r) -> r.endsWith(".json") || r.endsWith(".json5"));
                HashMap<Identifier, InkColor> loaded = new LinkedHashMap<>();

                for (Identifier fileId : inkColors) {
                    try (
                        InputStream is = manager.getResource(fileId).getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is))
                    ) {
                        JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
                        JsonElement colorElement = json.get("color");
                        int color = colorElement.getAsJsonPrimitive().isString()
                            ? Integer.parseInt(colorElement.getAsString().replaceFirst("#", ""), 16)
                            : colorElement.getAsInt();

                        String id = fileId.toString();
                        for (int i = 0; i < 2; i++) {
                            id = id.substring(id.indexOf("/") + 1);
                        }

                        InkColor inkColor = new InkColor(new Identifier(fileId.getNamespace(), id.substring(0, id.lastIndexOf("."))), color);
                        loaded.put(inkColor.id, inkColor);
                    } catch (Exception e) {
                        log(Level.ERROR, "Unable to load ink color from '" + fileId + "'.");
                        e.printStackTrace();
                    }
                }

                InkColors.rebuildIfNeeded(loaded);
                log("Loaded " + InkColors.getAll().size() + " ink colors!");

                if (SERVER_INSTANCE != null) {
                    InkColors.sync(PlayerLookup.all(SERVER_INSTANCE));
                }
            }
        });
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(Splatcraft.MOD_ID, "signal_whitelists");
            }

            @Override
            public void apply(ResourceManager manager) {
                Collection<Identifier> whitelists = manager.findResources(Splatcraft.MOD_ID, (r) -> r.equals("signal_whitelist.json") || r.equals("signal_whitelist.json5"));
                LinkedList<Identifier> loaded = new LinkedList<>();

                for (Identifier fileId : whitelists) {
                    try (
                        InputStream is = manager.getResource(fileId).getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is))
                    ) {
                        JsonArray json = new JsonParser().parse(reader).getAsJsonArray();
                        json.forEach(jsonElement -> loaded.add(Identifier.tryParse(jsonElement.getAsString())));
                    } catch (Exception e) {
                        log(Level.ERROR, "Unable to load signal whitelist from '" + fileId + "'.");
                        e.printStackTrace();
                    }
                }

                SignalWhitelistManager.loadWhitelist(loaded);
            }
        });

        // networking
        SplatcraftNetworking.registerReceivers();

        // player events
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> PlayerHandler.getEventActionResult(player));
        UseItemCallback.EVENT.register((player, world, hand) -> PlayerHandler.getEventActionResult(player, player.getStackInHand(hand)));
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> PlayerHandler.getEventActionResult(player));
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> PlayerHandler.getEventActionResult(player));
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> PlayerHandler.getEventActionResult(player));

        // server lifecycle events
        ServerLifecycleEvents.SERVER_STARTING.register(server -> SERVER_INSTANCE = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVER_INSTANCE = null);
        // server connection events
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LazyPlayerDataComponent.markForceSync(handler.player);
            InkColors.sync(handler, sender, server);
        });

        // commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            SetInkColorCommand.register(dispatcher);
            ScanTurfCommand.register(dispatcher);
            FillInkColorCommand.register(dispatcher);
            ClearInkColorCommand.register(dispatcher);
        });
        ArgumentTypes.register(new Identifier(Splatcraft.MOD_ID, "ink_color").toString(), InkColorArgumentType.class, new ConstantArgumentSerializer<>(InkColorArgumentType::inkColor));

        log("Initialized");
    }

    private static final String FORMATTED_MOD_NAME = "[" + MOD_NAME + "]";
    public static void log(Level level, String message) {
        LOGGER.log(level, FORMATTED_MOD_NAME + " " + message);
    }
    public static void log(String message) {
        log(Level.INFO, message);
    }
}
