package com.cibernet.splatcraft.client.renderer.entity.ink_squid;

import com.cibernet.splatcraft.client.model.entity.InkSquidEntityModel;
import com.cibernet.splatcraft.entity.InkSquidEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class InkSquidEntityColorFeatureRenderer extends FeatureRenderer<LivingEntity, InkSquidEntityModel> {
    public InkSquidEntityColorFeatureRenderer(FeatureRendererContext<LivingEntity, InkSquidEntityModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider consumers, int i, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        float[] color = ColorUtils.getColorsFromInt(ColorUtils.getEntityColor(entity).getColorOrLocked());
        FeatureRenderer.render(this.getContextModel(), new InkSquidEntityModel(), SplatcraftEntities.texture(InkSquidEntity.id + "/" + InkSquidEntity.id), matrixStack, consumers, i, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch, color[0], color[1], color[2]);
    }
}
