package net.splatcraft.impl.client;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.moddingplayground.frame.api.util.InitializationLogger;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.client.keybind.SplatcraftKeyBindings;
import net.splatcraft.api.client.model.SplatcraftEntityModelLayers;
import net.splatcraft.api.item.InkTankItem;
import net.splatcraft.impl.client.config.ClientCompatConfig;
import net.splatcraft.impl.client.config.ClientConfig;
import net.splatcraft.impl.client.keybind.SplatcraftDevelopmentKeyBindings;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public final class SplatcraftClientImpl implements Splatcraft, ClientModInitializer {
    private final InitializationLogger initializer;

    public SplatcraftClientImpl() {
        this.initializer = new InitializationLogger(LOGGER, MOD_NAME, EnvType.CLIENT);
        new Events();
    }

    @Override
    public void onInitializeClient() {
        this.initializer.start();

        Reflection.initialize(
            ClientConfig.class,
            ClientCompatConfig.class,

            SplatcraftEntityModelLayers.class,
            SplatcraftKeyBindings.class
        );

        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isDevelopmentEnvironment()) onInitializeClientDev();

        this.initializer.finish();
    }

    public void onInitializeClientDev() {
        InitializationLogger logger = new InitializationLogger(LOGGER, MOD_NAME + "-dev", EnvType.CLIENT);
        logger.start();
        Reflection.initialize(SplatcraftDevelopmentKeyBindings.class);
        logger.finish();
    }

    public static class Events {
        protected Events() {
            LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(this::allowCapeRender);
        }

        public boolean allowCapeRender(AbstractClientPlayerEntity player) {
            return !ClientConfig.INSTANCE.cancelCapeRenderWithInkTank.getValue() || !(player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof InkTankItem);
        }
    }
}
