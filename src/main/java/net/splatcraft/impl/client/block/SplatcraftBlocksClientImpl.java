package net.splatcraft.impl.client.block;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.splatcraft.api.block.SplatcraftBlocks;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.mixin.client.ClientWorldAccessor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static net.splatcraft.api.client.util.ClientUtil.*;

@Environment(EnvType.CLIENT)
public final class SplatcraftBlocksClientImpl implements SplatcraftBlocks, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap brlm = BlockRenderLayerMap.INSTANCE;
        brlm.putBlocks(RenderLayer.getCutout(), GRATE_BLOCK, GRATE, GRATE_RAMP, EMPTY_INKWELL, INKWELL);

        ColorProviderRegistry.BLOCK.register( // inkable block entity colors
            (state, world, pos, tintIndex) -> world != null && world.getBlockEntity(pos) instanceof Inkable inkable
                ? getDecimalColor(inkable.getInkColor())
                : InkColors.getDefault().getDecimalColor(),
            CANVAS, INKWELL, INKED_BLOCK, GLOWING_INKED_BLOCK
        );

        Set<Item> blockMarkers = new HashSet<>(ClientWorldAccessor.getBLOCK_MARKER_ITEMS());
        Collections.addAll(blockMarkers, STAGE_BARRIER.asItem(), STAGE_VOID.asItem());
        ClientWorldAccessor.setBLOCK_MARKER_ITEMS(blockMarkers);
    }
}
