package com.cibernet.splatcraft.client.renderer.entity.ink_squid;

import com.cibernet.splatcraft.client.model.InkSquidEntityModel;
import com.cibernet.splatcraft.config.SplatcraftConfig;
import com.cibernet.splatcraft.entity.InkSquidEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class InkSquidEntityColorFeatureRenderer extends FeatureRenderer<LivingEntity, InkSquidEntityModel> {
    public InkSquidEntityColorFeatureRenderer(FeatureRendererContext<LivingEntity, InkSquidEntityModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider consumers, int i, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        InkColor color = ColorUtils.getLivingEntityColor(entity);
        if (SplatcraftConfig.COLORS.colorLock.getBoolean()) {
            color = /*ColorUtils.getLockedColor(color)*/ InkColors.COLOR_LOCK_FRIENDLY;
        }

        int colorInt = color.getColor();
        float r = ((colorInt & 16711680) >> 16) / 255.0f;
        float g = ((colorInt & '\uff00') >> 8) / 255.0f;
        float b = (colorInt & 255) / 255.0f;

        FeatureRenderer.render(this.getContextModel(), new InkSquidEntityModel(), SplatcraftEntities.texture(InkSquidEntity.id + "/" + InkSquidEntity.id), matrixStack, consumers, i, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch, r, g, b);
    }
}
