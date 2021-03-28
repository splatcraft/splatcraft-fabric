package com.cibernet.splatcraft;

import com.cibernet.splatcraft.client.config.SplatcraftConfigManager;
import com.cibernet.splatcraft.client.init.SplatcraftKeyBindings;
import com.cibernet.splatcraft.client.model.ink_tank.AbstractInkTankArmorModel;
import com.cibernet.splatcraft.client.network.SplatcraftClientNetworking;
import com.cibernet.splatcraft.client.particle.InkSplashParticle;
import com.cibernet.splatcraft.client.renderer.InkProjectileEntityRenderer;
import com.cibernet.splatcraft.client.renderer.InkedBlockEntityRenderer;
import com.cibernet.splatcraft.client.renderer.StageBarrierBlockEntityRenderer;
import com.cibernet.splatcraft.client.renderer.entity.ink_squid.InkSquidEntityRenderer;
import com.cibernet.splatcraft.client.renderer.entity.squid_bumper.SquidBumperEntityRenderer;
import com.cibernet.splatcraft.init.*;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.item.InkTankArmorItem;
import com.cibernet.splatcraft.item.inkable.ColorLockItemColorProvider;
import com.cibernet.splatcraft.item.remote.RemoteItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;

@Environment(EnvType.CLIENT)
public class SplatcraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Splatcraft.log("Initializing client");

        InkColors.rebuildIfNeeded(new LinkedHashMap<>());
        SplatcraftConfigManager.load();
        new SplatcraftKeyBindings();

        // block entity renderers
        BlockEntityRendererRegistry berrIntance = BlockEntityRendererRegistry.INSTANCE;
        berrIntance.register(SplatcraftBlockEntities.STAGE_BARRIER, StageBarrierBlockEntityRenderer::new);
        berrIntance.register(SplatcraftBlockEntities.INKED_BLOCK, InkedBlockEntityRenderer::new);

        // entity renderers
        EntityRendererRegistry errInstance = EntityRendererRegistry.INSTANCE;
        errInstance.register(SplatcraftEntities.INK_SQUID, InkSquidEntityRenderer::new);
        errInstance.register(SplatcraftEntities.SQUID_BUMPER, SquidBumperEntityRenderer::new);
        errInstance.register(SplatcraftEntities.INK_PROJECTILE, InkProjectileEntityRenderer::new);

        // custom armor models
        ArmorRenderingRegistry.registerModel((entity, stack, slot, defaultModel) -> ((InkTankArmorItem) stack.getItem()).getArmorModel(entity, stack, slot, defaultModel), AbstractInkTankArmorModel.ITEM_TO_MODEL_MAP.keySet());

        // color providers
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world == null
            ? InkColors.NONE.getColor()
            : ColorUtils.getInkColor(world.getBlockEntity(pos)).getColorOrLocked(), SplatcraftBlocks.getInkables()
        );

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ColorUtils.getInkColor(stack).getColor(), SplatcraftBlocks.getInkables());
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0
            ? -1
            : stack.getItem() instanceof ColorLockItemColorProvider
                ? ColorUtils.getInkColor(stack).getColorOrLocked()
                : ColorUtils.getInkColor(stack).getColor(),
            SplatcraftItems.getInkables()
        );

        // model predicates
        for (Item item : new Item[] { SplatcraftItems.COLOR_CHANGER }) {
            registerModelPredicate(item, "active", (stack, world, entity) -> RemoteItem.hasCoordSet(stack) ? 1.0f : 0.0f);
            registerModelPredicate(item, "mode", (stack, world, entity) -> RemoteItem.getRemoteMode(stack));
        }
        registerUnfoldedModelPredicate(SplatcraftItems.SPLAT_ROLLER, SplatcraftItems.KRAK_ON_SPLAT_ROLLER, SplatcraftItems.COROCORO_SPLAT_ROLLER, SplatcraftItems.OCTOBRUSH, SplatcraftItems.CARBON_ROLLER, SplatcraftItems.INKBRUSH);

        // particles
        ParticleFactoryRegistry pfrInstance = ParticleFactoryRegistry.getInstance();
        pfrInstance.register(SplatcraftParticles.INK_SPLASH, InkSplashParticle.Factory::new);

        // block render layers
        BlockRenderLayerMap brlmInstance = BlockRenderLayerMap.INSTANCE;
        brlmInstance.putBlocks(RenderLayer.getTranslucent(), SplatcraftBlocks.GLOWING_INKED_BLOCK);
        brlmInstance.putBlocks(RenderLayer.getCutout(), SplatcraftBlocks.STAGE_BARRIER, SplatcraftBlocks.STAGE_VOID, SplatcraftBlocks.INKWELL/*, SplatcraftBlocks.EMPTY_INKWELL*/, SplatcraftBlocks.GRATE, SplatcraftBlocks.GRATE_RAMP/*, SplatcraftBlocks.CRATE, SplatcraftBlocks.SUNKEN_CRATE*/);

        // networking
        SplatcraftClientNetworking.registerReceivers();

        Splatcraft.log("Initialized client");
    }

    private static void registerModelPredicate(Item item, String id, ModelPredicateProvider provider) {
        FabricModelPredicateProviderRegistry.register(item, new Identifier(Splatcraft.MOD_ID, id), provider);
    }
    private static void registerUnfoldedModelPredicate(Item... items) {
        for (Item item : items) {
            registerModelPredicate(item, "unfolded", (stack, world, entity) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
        }
    }
}
