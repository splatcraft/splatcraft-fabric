package net.splatcraft;

import com.google.common.reflect.Reflection;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.block.entity.SplatcraftBannerPatterns;
import net.splatcraft.block.entity.SplatcraftBlockEntities;
import net.splatcraft.command.InkColorCommand;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.component.SplatcraftComponents;
import net.splatcraft.config.CommonConfig;
import net.splatcraft.entity.SplatcraftEntities;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.network.NetworkingCommon;
import net.splatcraft.particle.SplatcraftParticles;
import net.splatcraft.registry.SplatcraftRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.splatcraft.util.SplatcraftUtil.canSubmergeInInk;
import static net.splatcraft.util.SplatcraftUtil.refreshSplatfestBand;

public class Splatcraft implements ModInitializer {
    public static final String MOD_ID   = "splatcraft";
    public static final String MOD_NAME = "Splatcraft";
    public static final Logger LOGGER   = LogManager.getLogger(MOD_ID);

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing {}", MOD_NAME);

        Reflection.initialize(
            CommonConfig.class,

            SplatcraftRegistries.class,
            InkColors.class,

            SplatcraftParticles.class,

            SplatcraftBannerPatterns.class,
            SplatcraftBlockEntities.class,
            SplatcraftBlocks.class,
            SplatcraftItems.class,
            SplatcraftComponents.class,
            SplatcraftEntities.class,

            NetworkingCommon.class
        );

        CommandRegistrationCallback.EVENT.register(Splatcraft::registerCommands);
        ServerPlayConnectionEvents.JOIN.register(Splatcraft::onPlayerJoin);
        ServerTickEvents.START_SERVER_TICK.register(Splatcraft::onStartTick);

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) initDev();

        LOGGER.info("Initialized {}", MOD_NAME);
    }

    private void initDev() {
        LOGGER.info("Initializing {}-dev", Splatcraft.MOD_NAME);

        for (Registry<?> registry : Registry.REGISTRIES) {
            Identifier registryId = registry.getKey().getValue();
            if (registryId.getNamespace().equals(MOD_ID)) {
                LOGGER.info("registry {}: {}", registryId.getPath(), registry.getEntries().size());
            }
        }

        LOGGER.info("Initialized {}-dev", Splatcraft.MOD_NAME);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        InkColorCommand.register(dispatcher);
    }

    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        refreshSplatfestBand(handler.player);
    }

    private static void onStartTick(MinecraftServer server) {
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            data.setSubmerged(canSubmergeInInk(player));
        }
    }
}
