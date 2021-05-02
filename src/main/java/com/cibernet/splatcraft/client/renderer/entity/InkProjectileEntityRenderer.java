package com.cibernet.splatcraft.client.renderer.entity;

import com.cibernet.splatcraft.client.model.entity.InkProjectileEntityModel;
import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class InkProjectileEntityRenderer extends EntityRenderer<InkProjectileEntity> {
    private static final Identifier TEXTURE = SplatcraftEntities.texture(InkProjectileEntity.id);
    private final InkProjectileEntityModel MODEL = new InkProjectileEntityModel();

    @SuppressWarnings("unused")
    public InkProjectileEntityRenderer(EntityRenderDispatcher manager, @Nullable EntityRendererRegistry.Context ctx) {
        super(manager);
    }

    @Override
    public void render(InkProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.age >= 3 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25d)) {
            matrices.push();

            float[] color = ColorUtil.getColorsFromInt(entity.getInkColor().getColorOrLocked());
            float scale = entity.getProjectileSize();
            matrices.scale(scale, scale, scale);

            this.MODEL.render(matrices, vertexConsumers.getBuffer(this.MODEL.getLayer(TEXTURE)), light, OverlayTexture.DEFAULT_UV, color[0], color[1], color[2], 1);

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
