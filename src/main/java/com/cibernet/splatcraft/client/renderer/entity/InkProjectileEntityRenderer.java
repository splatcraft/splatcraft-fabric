package com.cibernet.splatcraft.client.renderer.entity;

import com.cibernet.splatcraft.client.init.SplatcraftEntityModelLayers;
import com.cibernet.splatcraft.client.model.entity.InkProjectileEntityModel;
import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class InkProjectileEntityRenderer extends EntityRenderer<InkProjectileEntity> {
    private static final Identifier TEXTURE = SplatcraftEntities.texture(InkProjectileEntity.id);
    private final InkProjectileEntityModel model;

    @SuppressWarnings("unused")
    public InkProjectileEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new InkProjectileEntityModel(ctx.getPart(SplatcraftEntityModelLayers.INK_PROJECTILE));
    }

    @Override
    public void render(InkProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.age >= 3 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25d)) {
            matrices.push();

            float[] color = entity.getInkColor().getColorOrLockedComponents();
            float scale = entity.getProjectileSize();
            matrices.scale(scale, scale, scale);

            this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer(TEXTURE)), light, OverlayTexture.DEFAULT_UV, color[0], color[1], color[2], 1);

            matrices.pop();
        }
    }

    @Override
    public Identifier getTexture(InkProjectileEntity entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(InkProjectileEntity entity, BlockPos blockPos) {
        return entity.getInkType().isGlowing() ? 15 : super.getBlockLight(entity, blockPos);
    }
}
