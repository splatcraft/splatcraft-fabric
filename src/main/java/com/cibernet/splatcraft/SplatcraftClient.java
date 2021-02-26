package com.cibernet.splatcraft;

import com.cibernet.splatcraft.client.init.SplatcraftKeyBindings;
import com.cibernet.splatcraft.client.renderer.StageBarrierBlockEntityRenderer;
import com.cibernet.splatcraft.client.renderer.entity.ink_squid.InkSquidEntityRenderer;
import com.cibernet.splatcraft.config.SplatcraftConfigManager;
import com.cibernet.splatcraft.init.*;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.item.RemoteItem;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class SplatcraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Splatcraft.log("Initializing client");

        SplatcraftConfigManager.load();
        new SplatcraftKeyBindings();

        // block entity renderers
        BlockEntityRendererRegistry berrIntance = BlockEntityRendererRegistry.INSTANCE;
        // berrIntance.register(SplatcraftBlockEntities.INKED_BLOCK, InkedBlockEntityRenderer::new);
        berrIntance.register(SplatcraftBlockEntities.STAGE_BARRIER, StageBarrierBlockEntityRenderer::new);

        // entity renderers
        EntityRendererRegistry errInstance = EntityRendererRegistry.INSTANCE;
        errInstance.register(SplatcraftEntities.INK_SQUID, InkSquidEntityRenderer::new);

        // color providers
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world == null ? InkColors.NONE.getColor() : ColorUtils.getInkColor(world.getBlockEntity(pos)).getColor(), SplatcraftBlocks.getInkableBlocks());
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ColorUtils.getInkColor(stack).getColor(), SplatcraftBlocks.getInkableBlocks());
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ColorUtils.getInkColor(stack).getColor(), SplatcraftItems.getInkableItems());

        // model predicates
        for (Item item : new Item[] { SplatcraftItems.COLOR_CHANGER }) {
            registerModelPredicate(item, "active", (stack, world, entity) -> RemoteItem.hasCoordSet(stack) ? 1.0F : 0.0F);
            registerModelPredicate(item, "mode", (stack, world, entity) -> RemoteItem.getRemoteMode(stack));
        }

        // block render layers
        BlockRenderLayerMap brlmInstance = BlockRenderLayerMap.INSTANCE;
        brlmInstance.putBlocks(RenderLayer.getTranslucent(), SplatcraftBlocks.GLOWING_INKED_BLOCK);
        brlmInstance.putBlocks(RenderLayer.getCutout(), SplatcraftBlocks.INKWELL/*, SplatcraftBlocks.EMPTY_INKWELL*/, SplatcraftBlocks.GRATE/*, SplatcraftBlocks.GRATE_RAMP, SplatcraftBlocks.CRATE, SplatcraftBlocks.SUNKEN_CRATE*/);

        // networking
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SET_PLAYER_INK_COLOR_PACKET_ID, (client, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            String inkColorId = buf.readString();

            client.execute(() -> ColorUtils.setInkColor(MinecraftClient.getInstance().world.getPlayerByUuid(uuid), SplatcraftRegistries.INK_COLORS.get(new Identifier(inkColorId))));
        });
        ClientPlayNetworking.registerGlobalReceiver(SplatcraftNetworkingConstants.SET_BLOCK_ENTITY_INK_COLOR_PACKET_ID, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String inkColorId = buf.readString();

            client.execute(() -> ColorUtils.setInkColor(MinecraftClient.getInstance().world.getBlockEntity(pos), SplatcraftRegistries.INK_COLORS.get(new Identifier(inkColorId))));
        });

        Splatcraft.log("Initialized client");
    }

    private static void registerModelPredicate(Item item, String id, ModelPredicateProvider provider) {
        FabricModelPredicateProviderRegistry.register(item, new Identifier(Splatcraft.MOD_ID, id), provider);
    }
}
