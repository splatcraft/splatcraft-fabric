package com.cibernet.splatcraft;

import com.cibernet.splatcraft.client.config.SplatcraftConfigManager;
import com.cibernet.splatcraft.client.init.SplatcraftEntityModelLayers;
import com.cibernet.splatcraft.client.init.SplatcraftKeyBindings;
import com.cibernet.splatcraft.client.model.entity.InkProjectileEntityModel;
import com.cibernet.splatcraft.client.model.entity.InkSquidEntityModel;
import com.cibernet.splatcraft.client.model.entity.SquidBumperEntityModel;
import com.cibernet.splatcraft.client.network.SplatcraftClientNetworking;
import com.cibernet.splatcraft.client.particle.InkSplashParticle;
import com.cibernet.splatcraft.client.particle.InkSquidSoulParticle;
import com.cibernet.splatcraft.client.particle.WaxInkedBlockParticle;
import com.cibernet.splatcraft.client.renderer.block.entity.InkedBlockEntityRenderer;
import com.cibernet.splatcraft.client.renderer.block.entity.StageBarrierBlockEntityRenderer;
import com.cibernet.splatcraft.client.renderer.entity.InkProjectileEntityRenderer;
import com.cibernet.splatcraft.client.renderer.entity.ink_squid.InkSquidEntityRenderer;
import com.cibernet.splatcraft.client.renderer.entity.squid_bumper.SquidBumperEntityRenderer;
import com.cibernet.splatcraft.client.signal.Signal;
import com.cibernet.splatcraft.client.signal.SignalRegistryManager;
import com.cibernet.splatcraft.client.signal.SignalRendererManager;
import com.cibernet.splatcraft.init.*;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.inkcolor.InkColorSynchroniser;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.item.inkable.ColorLockItemColorProvider;
import com.cibernet.splatcraft.item.remote.AbstractRemoteItem;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Reflection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.item.Item;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Environment(EnvType.CLIENT)
public class SplatcraftClient implements ClientModInitializer {
    @SuppressWarnings({"UnstableApiUsage","deprecation"})
    @Override
    public void onInitializeClient() {
        Splatcraft.log("Initializing client");

        // init
        Reflection.initialize(
            SplatcraftKeyBindings.class,
            SplatcraftConfigManager.class
        );

        // ink colors
        InkColorSynchroniser.rebuildIfNeeded(new LinkedHashMap<>());

        // block entity renderers
        BlockEntityRendererRegistry berrIntance = BlockEntityRendererRegistry.INSTANCE;
        berrIntance.register(SplatcraftBlockEntities.STAGE_BARRIER, StageBarrierBlockEntityRenderer::new);
        berrIntance.register(SplatcraftBlockEntities.INKED_BLOCK, InkedBlockEntityRenderer::new);

        // entity renderers
        EntityRendererRegistry errInstance = EntityRendererRegistry.INSTANCE;
        errInstance.register(SplatcraftEntities.INK_SQUID, InkSquidEntityRenderer::new);
        errInstance.register(SplatcraftEntities.SQUID_BUMPER, SquidBumperEntityRenderer::new);
        errInstance.register(SplatcraftEntities.INK_PROJECTILE, InkProjectileEntityRenderer::new);

        ImmutableMap.<EntityModelLayer, EntityModelLayerRegistry.TexturedModelDataProvider>of(
            SplatcraftEntityModelLayers.INK_PROJECTILE, InkProjectileEntityModel::getTexturedModelData,
            SplatcraftEntityModelLayers.INK_SQUID, InkSquidEntityModel::getTexturedModelData,
            SplatcraftEntityModelLayers.SQUID_BUMPER, SquidBumperEntityModel::getTexturedModelData
        ).forEach(EntityModelLayerRegistry::registerModelLayer);

        // custom armor models
        // ArmorRenderingRegistry.registerModel((entity, stack, slot, defaultModel) -> ((InkTankArmorItem) stack.getItem()).getArmorModel(entity, stack, slot, defaultModel), AbstractInkTankArmorModel.ITEM_TO_MODEL_MAP.keySet());

        // color providers
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
                if (world != null && pos != null) {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    return ColorUtil.getInkColor(blockEntity != null ? blockEntity : world.getBlockEntity(pos.down())).getColorOrLocked();
                }

                return InkColors.NONE.color;
            }, SplatcraftBlocks.getInkables()
        );

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ColorUtil.getInkColor(stack).color, SplatcraftBlocks.getInkables());
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0
            ? -1
            : stack.getItem() instanceof ColorLockItemColorProvider
                ? ColorUtil.getInkColor(stack).getColorOrLocked()
                : ColorUtil.getInkColor(stack).color,
            SplatcraftItems.getInkables()
        );

        // model predicates
        for (Item item : new Item[] { SplatcraftItems.COLOR_CHANGER, SplatcraftItems.INK_DISRUPTOR, SplatcraftItems.TURF_SCANNER }) {
            registerModelPredicate(item, "active", (stack, world, entity, seed) -> AbstractRemoteItem.hasCornerPair(stack) ? 1.0f : 0.0f);
            registerModelPredicate(item, "mode", (stack, world, entity, seed) -> {
                Item pItem = stack.getItem();
                if (pItem instanceof AbstractRemoteItem) {
                    return ((AbstractRemoteItem) pItem).getRemoteModeOrdinal(stack);
                }

                return 0;
            });
        }
        registerUnfoldedModelPredicate(SplatcraftItems.SPLAT_ROLLER, SplatcraftItems.KRAK_ON_SPLAT_ROLLER, SplatcraftItems.COROCORO_SPLAT_ROLLER, SplatcraftItems.OCTOBRUSH, SplatcraftItems.CARBON_ROLLER, SplatcraftItems.INKBRUSH);

        // assets
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(Splatcraft.MOD_ID, "signals");
            }

            @Override
            public void reload(ResourceManager manager) {
                Collection<Identifier> signals = manager.findResources("animations/" + Splatcraft.MOD_ID + "/signals", (r) -> r.endsWith(".json") || r.endsWith(".json5"));
                HashMap<Identifier, Signal> loaded = new LinkedHashMap<>();

                for (Identifier fileId : signals) {
                    try {
                        String id = fileId.toString();
                        for (int i = 0; i < 3; i++) {
                            id = id.substring(id.indexOf("/") + 1);
                        }
                        Signal signal = new Signal(new Identifier(fileId.getNamespace(), id.substring(0, id.lastIndexOf("."))));

                        loaded.put(signal.id, signal);
                    } catch (Exception e) {
                        Splatcraft.log(Level.ERROR, "Unable to load signal from '" + fileId + "'.");
                        e.printStackTrace();
                    }
                }

                SignalRegistryManager.loadFromAssets(loaded);
                Splatcraft.log("Loaded " + SignalRegistryManager.SIGNALS.size() + " signals!");
            }
        });

        // particles
        ParticleFactoryRegistry pfrInstance = ParticleFactoryRegistry.getInstance();
        pfrInstance.register(SplatcraftParticles.INK_SPLASH, InkSplashParticle.Factory::new);
        pfrInstance.register(SplatcraftParticles.INK_SQUID_SOUL, InkSquidSoulParticle.Factory::new);
        for (DefaultParticleType particleType : new DefaultParticleType[]{ SplatcraftParticles.WAX_INKED_BLOCK_ON, SplatcraftParticles.WAX_INKED_BLOCK_OFF }) {
            pfrInstance.register(particleType, WaxInkedBlockParticle.Factory::new);
        }

        // block render layers
        BlockRenderLayerMap brlmInstance = BlockRenderLayerMap.INSTANCE;
        brlmInstance.putBlocks(RenderLayer.getTranslucent(), SplatcraftBlocks.GLOWING_INKED_BLOCK);
        brlmInstance.putBlocks(RenderLayer.getCutout(), SplatcraftBlocks.STAGE_BARRIER, SplatcraftBlocks.STAGE_VOID, SplatcraftBlocks.INKWELL/*, SplatcraftBlocks.EMPTY_INKWELL*/, SplatcraftBlocks.GRATE, SplatcraftBlocks.GRATE_RAMP/*, SplatcraftBlocks.CRATE, SplatcraftBlocks.SUNKEN_CRATE*/);

        // networking
        SplatcraftClientNetworking.registerReceivers();

        // player events
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> SignalRendererManager.reset(player));
        UseItemCallback.EVENT.register((player, world, hand) -> SignalRendererManager.reset(player, player.getStackInHand(hand)));
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> SignalRendererManager.reset(player));
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> SignalRendererManager.reset(player));
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> SignalRendererManager.reset(player));

        Splatcraft.log("Initialized client");
    }

    private static void registerModelPredicate(Item item, String id, UnclampedModelPredicateProvider provider) {
        FabricModelPredicateProviderRegistry.register(item, new Identifier(Splatcraft.MOD_ID, id), provider);
    }
    private static void registerUnfoldedModelPredicate(Item... items) {
        for (Item item : items) {
            registerModelPredicate(item, "unfolded", (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
        }
    }

    public static Identifier texture(String path) {
        return new Identifier(Splatcraft.MOD_ID, "textures/" + path + ".png");
    }
}
