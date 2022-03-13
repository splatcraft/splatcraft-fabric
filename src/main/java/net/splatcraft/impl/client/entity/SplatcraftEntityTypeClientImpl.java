package net.splatcraft.impl.client.entity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.splatcraft.api.client.render.entity.InkSquidEntityModelRenderer;
import net.splatcraft.api.entity.SplatcraftEntityType;
import net.splatcraft.impl.client.render.entity.InkProjectileEntityRenderer;

@Environment(EnvType.CLIENT)
public final class SplatcraftEntityTypeClientImpl implements SplatcraftEntityType, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(INK_SQUID, InkSquidEntityModelRenderer::new);
        EntityRendererRegistry.register(INK_PROJECTILE, InkProjectileEntityRenderer::new);
    }
}
