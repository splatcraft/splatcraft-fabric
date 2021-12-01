package net.splatcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.splatcraft.block.SplatcraftBlocks;

public class SplatcraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap brlm = BlockRenderLayerMap.INSTANCE;
        brlm.putBlocks(RenderLayer.getCutout(),
            SplatcraftBlocks.GRATE
        );
    }
}
