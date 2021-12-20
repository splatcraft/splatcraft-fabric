package net.splatcraft.client;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
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
import net.splatcraft.client.render.block.inkable.InkedBlockEntityRenderer;
import net.splatcraft.client.render.entity.inkable.InkSquidEntityModelRenderer;
import net.splatcraft.entity.InkableCaster;
import net.splatcraft.entity.SplatcraftEntities;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.mixin.client.ClientWorldAccessor;
import net.splatcraft.particle.SplatcraftParticles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static net.splatcraft.util.SplatcraftUtil.getDecimalColor;
import static net.splatcraft.util.SplatcraftUtil.getInkColorFromStack;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public class SplatcraftClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("%s-client".formatted(Splatcraft.MOD_ID));

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
            SplatcraftBlocks.GRATE_BLOCK,
            SplatcraftBlocks.GRATE,
            SplatcraftBlocks.EMPTY_INKWELL,
            SplatcraftBlocks.INKWELL
        );

        // entities
        EntityRendererRegistry.register(SplatcraftEntities.INK_SQUID, InkSquidEntityModelRenderer::new);

        // block entities
        BlockEntityRendererRegistry.register(SplatcraftBlockEntities.INKED_BLOCK, InkedBlockEntityRenderer::new);

        // model predicates
        modelPredicateUsing(SplatcraftItems.SPLAT_ROLLER);

        // color providers
        ColorProviderRegistry.BLOCK.register( // inkable block entity colors
            (state, world, pos, tintIndex) -> world != null && world.getBlockEntity(pos) instanceof Inkable inkable
                ? getDecimalColor(inkable.getInkColor())
                : InkColors._DEFAULT.getDecimalColor(),
            SplatcraftBlocks.CANVAS, SplatcraftBlocks.INKWELL, SplatcraftBlocks.INKED_BLOCK, SplatcraftBlocks.GLOWING_INKED_BLOCK
        );

        ColorProviderRegistry.ITEM.register( // inkable items
            (stack, tintIndex) -> getInkColorFromStack(stack).getDecimalColor(),
            SplatcraftBlocks.CANVAS, SplatcraftBlocks.INKWELL
        );

        ColorProviderRegistry.ITEM.register( // inking items
            (stack, tintIndex) -> {
                MinecraftClient client = MinecraftClient.getInstance();
                return client.player instanceof InkableCaster caster
                    ? getDecimalColor(caster.toInkable().getInkColor())
                    : getInkColorFromStack(stack).getDecimalColor();
            },
            SplatcraftItems.SPLAT_ROLLER
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

    private static void modelPredicate(Item item, String id, UnclampedModelPredicateProvider provider) {
        FabricModelPredicateProviderRegistry.register(item, new Identifier(Splatcraft.MOD_ID, id), provider);
    }

    private static void modelPredicateUsing(Item... items) {
        for (Item item : items) {
            modelPredicate(
                item, "using",
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1 : 0
            );
        }
    }
}
