package com.cibernet.splatcraft.client.renderer.entity.ink_squid;

import com.cibernet.splatcraft.client.init.SplatcraftEntityModelLayers;
import com.cibernet.splatcraft.client.model.entity.InkSquidEntityModel;
import com.cibernet.splatcraft.entity.InkSquidEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class InkSquidEntityColorFeatureRenderer extends FeatureRenderer<LivingEntity, InkSquidEntityModel> {
    private final InkSquidEntityModel model;

    public InkSquidEntityColorFeatureRenderer(FeatureRendererContext<LivingEntity, InkSquidEntityModel> ctx, EntityModelLoader loader) {
        super(ctx);
        this.model = new InkSquidEntityModel(loader.getModelPart(SplatcraftEntityModelLayers.INK_SQUID));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider consumers, int i, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        float[] color = ColorUtil.getEntityColor(entity).getColorOrLockedComponents();
        FeatureRenderer.render(this.getContextModel(), this.model, SplatcraftEntities.texture(InkSquidEntity.id + "/" + InkSquidEntity.id), matrixStack, consumers, i, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch, color[0], color[1], color[2]);
    }
}
