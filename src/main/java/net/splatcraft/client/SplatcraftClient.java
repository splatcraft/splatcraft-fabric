package net.splatcraft.client;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
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
import net.splatcraft.client.render.entity.InkProjectileEntityRenderer;
import net.splatcraft.client.render.entity.InkSquidEntityModelRenderer;
import net.splatcraft.client.render.entity.InkTankRenderer;
import net.splatcraft.entity.SplatcraftEntities;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.item.InkTankItem;
import net.splatcraft.item.SplatcraftItems;
import net.splatcraft.mixin.client.ClientWorldAccessor;
import net.splatcraft.particle.SplatcraftParticles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static net.splatcraft.client.util.ClientUtil.*;
import static net.splatcraft.item.InkTankItem.*;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public class SplatcraftClient implements ClientModInitializer {
    private static SplatcraftClient instance = null;

    protected final Logger logger;
    protected final Events events;

    public SplatcraftClient() {
        this.logger = LogManager.getLogger("%s-client".formatted(Splatcraft.MOD_ID));
        this.events = new Events();
        instance = this;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onInitializeClient() {
        this.logger.info("Initializing {}-client", Splatcraft.MOD_NAME);

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
        EntityRendererRegistry.register(SplatcraftEntities.INK_PROJECTILE, InkProjectileEntityRenderer::new);

        // block entities
        BlockEntityRendererRegistry.register(SplatcraftBlockEntities.INKED_BLOCK, InkedBlockEntityRenderer::new);

        // armor
        ArmorRenderer.register(InkTankRenderer.INSTANCE::render, SplatcraftItems.INK_TANK);

        // model predicates
        modelPredicate("using",
            (stack, world, entity, seed) -> {
                return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1 : 0;
            }, SplatcraftItems.SPLAT_ROLLER
        );
        modelPredicate("contained_ink",
            (stack, world, entity, seed) -> {
                float containedInk = getContainedInk(stack);
                float capacity = ((InkTankItem) stack.getItem()).getCapacity();
                return containedInk / capacity;
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
                Inkable inkable = Inkable.class.cast(stack);
                return tintIndex == 0
                    ? stack.getItem() instanceof BlockItem
                        ? inkable.getInkColor().getDecimalColor()
                        : getDecimalColor(inkable.getInkColor())
                    : 0xFFFFFF;
            },
            SplatcraftBlocks.CANVAS, SplatcraftBlocks.INKWELL,

            SplatcraftItems.INK_CLOTH_HELMET, SplatcraftItems.INK_CLOTH_CHESTPLATE,
            SplatcraftItems.INK_CLOTH_LEGGINGS, SplatcraftItems.INK_CLOTH_BOOTS,
            SplatcraftItems.INK_TANK,

            SplatcraftItems.SPLAT_ROLLER, SplatcraftItems.SPLATTERSHOT
        );

        // block markers
        Set<Item> newBlockMarkerItems = new HashSet<>(ClientWorldAccessor.getBLOCK_MARKER_ITEMS());
        Collections.addAll(newBlockMarkerItems,
            SplatcraftBlocks.STAGE_BARRIER.asItem(),
            SplatcraftBlocks.STAGE_VOID.asItem()
        );
        ClientWorldAccessor.setBLOCK_MARKER_ITEMS(newBlockMarkerItems);

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) onInitializeClientDev();

        this.logger.info("Initialized {}-client", Splatcraft.MOD_NAME);
    }

    public void onInitializeClientDev() {
        this.logger.info("Initializing {}-client-dev", Splatcraft.MOD_NAME);
        Reflection.initialize(SplatcraftDevelopmentKeyBindings.class);
        this.logger.info("Initialized {}-client-dev", Splatcraft.MOD_NAME);
    }

    protected static void modelPredicate(String id, UnclampedModelPredicateProvider provider, Item... items) {
        for (Item item : items) {
            FabricModelPredicateProviderRegistry.register(item, new Identifier(Splatcraft.MOD_ID, id), provider);
        }
    }

    public Events getEvents() {
        return this.events;
    }

    public static SplatcraftClient getInstance() {
        return instance;
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
