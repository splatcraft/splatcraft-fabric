package net.splatcraft;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.block.entity.SplatcraftBannerPatterns;
import net.splatcraft.block.entity.SplatcraftBlockEntities;
import net.splatcraft.component.SplatcraftComponents;
import net.splatcraft.entity.SplatcraftEntities;
import net.splatcraft.entity.data.SplatcraftTrackedDataHandlers;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.network.NetworkingCommon;
import net.splatcraft.particle.SplatcraftParticles;
import net.splatcraft.registry.SplatcraftRegistries;
import net.splatcraft.util.Events;
import net.splatcraft.world.SplatcraftGameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Splatcraft implements ModInitializer {
    public static final String MOD_ID   = "splatcraft";
    public static final String MOD_NAME = "Splatcraft";
    public static final Logger LOGGER   = LogManager.getLogger(MOD_ID);

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing {}", MOD_NAME);

        Reflection.initialize(
            SplatcraftRegistries.class,
            InkColors.class,

            SplatcraftParticles.class,
            SplatcraftGameRules.class,

            SplatcraftBannerPatterns.class,
            SplatcraftBlockEntities.class,
            SplatcraftBlocks.class,
            SplatcraftItems.class,
            SplatcraftComponents.class,
            SplatcraftTrackedDataHandlers.class,
            SplatcraftEntities.class,

            NetworkingCommon.class
        );

        CommandRegistrationCallback.EVENT.register(Events::registerCommands);
        ServerPlayConnectionEvents.JOIN.register(Events::playerJoin);

        UseBlockCallback.EVENT.register(Events::useBlock);
        UseItemCallback.EVENT.register(Events::useItem);
        UseEntityCallback.EVENT.register(Events::useEntity);
        AttackEntityCallback.EVENT.register(Events::attackEntity);
        AttackBlockCallback.EVENT.register(Events::attackBlock);

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

}
