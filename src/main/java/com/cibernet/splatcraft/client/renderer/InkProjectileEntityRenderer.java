package com.cibernet.splatcraft.client.renderer;

import com.cibernet.splatcraft.client.model.InkProjectileEntityModel;
import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class InkProjectileEntityRenderer extends EntityRenderer<InkProjectileEntity> {
    private static final Identifier TEXTURE = SplatcraftEntities.texture(InkProjectileEntity.id);
    private final InkProjectileEntityModel MODEL = new InkProjectileEntityModel();

    @SuppressWarnings("unused")
    public InkProjectileEntityRenderer(EntityRenderDispatcher manager, @Nullable EntityRendererRegistry.Context ctx) {
        super(manager);
    }

    @Override
    public void render(InkProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity.getPos().add(0.0D, entity.getStandingEyeHeight(), 0.0D)) >= 4.0D) {
            float scale = entity.getProjectileSize();
            int color = entity.getInkColor().getColor();

            /*if (SplatcraftConfig.COLORS.colorLock.getBoolean()) {
                color = ColorUtils.getLockedColor(color);
            } TODO */

            float r = (float) (Math.floor(color / (256.0F * 256.0F)) / 255.0F);
            float g = (float) ((Math.floor(color / 256.0F) % 256.0F) / 255.0F);
            float b = (color % 256) / 255f;

            matrices.push();
            matrices.scale(scale, scale, scale);
            this.MODEL.render(matrices, vertexConsumers.getBuffer(this.MODEL.getLayer(TEXTURE)), light, OverlayTexture.DEFAULT_UV, r, g, b, 1);
            matrices.pop();
        }
    }

    @Override
    public Identifier getTexture(InkProjectileEntity entity) {
        return TEXTURE;
    }
}
