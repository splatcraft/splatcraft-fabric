package net.splatcraft.impl.client.block.entity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.splatcraft.api.block.entity.SplatcraftBlockEntityType;
import net.splatcraft.impl.client.render.block.InkedBlockEntityRenderer;

@Environment(EnvType.CLIENT)
public final class SplatcraftBlockEntityTypeClientImpl implements SplatcraftBlockEntityType, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(INKED_BLOCK, InkedBlockEntityRenderer::new);
    }
}
