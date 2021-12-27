package net.splatcraft.client;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.block.entity.SplatcraftBlockEntities;
import net.splatcraft.client.config.ClientCompatConfig;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.client.keybind.SplatcraftDevelopmentKeyBindings;
import net.splatcraft.client.keybind.SplatcraftKeyBindings;
import net.splatcraft.client.model.SplatcraftEntityModelLayers;
import net.splatcraft.client.network.NetworkingClient;
import net.splatcraft.client.particle.InkSplashParticle;
import net.splatcraft.client.particle.InkSquidSoulParticle;
import net.splatcraft.client.render.block.InkedBlockEntityRenderer;
import net.splatcraft.client.render.entity.InkSquidEntityModelRenderer;
import net.splatcraft.client.render.entity.InkTankRenderer;
import net.splatcraft.entity.SplatcraftEntities;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.item.InkTankItem;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.mixin.client.ClientWorldAccessor;
import net.splatcraft.particle.SplatcraftParticles;
import net.splatcraft.util.Events;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static net.splatcraft.client.util.ClientUtil.getDecimalColor;
import static net.splatcraft.item.InkTankItem.getContainedInk;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public class SplatcraftClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("%s-client".formatted(Splatcraft.MOD_ID));

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {}-client", Splatcraft.MOD_NAME);

        Reflection.initialize(
            ClientConfig.class,
            ClientCompatConfig.class,

            SplatcraftEntityModelLayers.class,
            SplatcraftKeyBindings.class,
            NetworkingClient.class
        );

        // particles
        ParticleFactoryRegistry pfrInstance = ParticleFactoryRegistry.getInstance();
        pfrInstance.register(SplatcraftParticles.INK_SPLASH, InkSplashParticle.Factory::new);
        pfrInstance.register(SplatcraftParticles.INK_SQUID_SOUL, InkSquidSoulParticle.Factory::new);

        // render layers
        BlockRenderLayerMap brlm = BlockRenderLayerMap.INSTANCE;
        brlm.putBlocks(RenderLayer.getCutout(),
            SplatcraftBlocks.GRATE_BLOCK, SplatcraftBlocks.GRATE,
            SplatcraftBlocks.EMPTY_INKWELL, SplatcraftBlocks.INKWELL
        );

        // entities
        EntityRendererRegistry.register(SplatcraftEntities.INK_SQUID, InkSquidEntityModelRenderer::new);

        // block entities
        BlockEntityRendererRegistry.register(SplatcraftBlockEntities.INKED_BLOCK, InkedBlockEntityRenderer::new);

        // armor
        ArmorRenderer.register(InkTankRenderer.INSTANCE::render, SplatcraftItems.INK_TANK);
        LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(Events::allowCapeRender);

        // model predicates
        modelPredicate("using",
            (stack, world, entity, seed) -> {
                return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1 : 0;
            }, SplatcraftItems.SPLAT_ROLLER
        );
        modelPredicate("contained_ink",
            (stack, world, entity, seed) -> {
                int containedInk = getContainedInk(stack);
                int capacity = ((InkTankItem) stack.getItem()).getCapacity();
                return (float) containedInk / capacity;
            }, SplatcraftItems.INK_TANK
        );

        // color providers
        ColorProviderRegistry.BLOCK.register( // inkable block entity colors
            (state, world, pos, tintIndex) -> world != null && world.getBlockEntity(pos) instanceof Inkable inkable
                ? getDecimalColor(inkable.getInkColor())
                : InkColors.getDefault().getDecimalColor(),
            SplatcraftBlocks.CANVAS, SplatcraftBlocks.INKWELL,
            SplatcraftBlocks.INKED_BLOCK, SplatcraftBlocks.GLOWING_INKED_BLOCK
        );

        ColorProviderRegistry.ITEM.register( // inkable items
            (stack, tintIndex) -> {
                return tintIndex == 0 ? Inkable.class.cast(stack).getInkColor().getDecimalColor() : 0xFFFFFF;
            },
            SplatcraftItems.INK_CLOTH_HELMET, SplatcraftItems.INK_CLOTH_CHESTPLATE,
            SplatcraftItems.INK_CLOTH_LEGGINGS, SplatcraftItems.INK_CLOTH_BOOTS,
            SplatcraftItems.INK_TANK, SplatcraftItems.SPLAT_ROLLER,

            SplatcraftBlocks.CANVAS, SplatcraftBlocks.INKWELL
        );

        // block markers
        Set<Item> newBlockMarkerItems = new HashSet<>(ClientWorldAccessor.getBlockMarkerItems());
        Collections.addAll(newBlockMarkerItems,
            SplatcraftBlocks.STAGE_BARRIER.asItem(),
            SplatcraftBlocks.STAGE_VOID.asItem()
        );
        ClientWorldAccessor.setBlockMarkerItems(newBlockMarkerItems);

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) initDev();

        LOGGER.info("Initialized {}-client", Splatcraft.MOD_NAME);
    }

    private void initDev() {
        LOGGER.info("Initializing {}-client-dev", Splatcraft.MOD_NAME);
        Reflection.initialize(SplatcraftDevelopmentKeyBindings.class);
        LOGGER.info("Initialized {}-client-dev", Splatcraft.MOD_NAME);
    }

    private static void modelPredicate(String id, UnclampedModelPredicateProvider provider, Item... items) {
        for (Item item : items) {
            FabricModelPredicateProviderRegistry.register(item, new Identifier(Splatcraft.MOD_ID, id), provider);
        }
    }
}
