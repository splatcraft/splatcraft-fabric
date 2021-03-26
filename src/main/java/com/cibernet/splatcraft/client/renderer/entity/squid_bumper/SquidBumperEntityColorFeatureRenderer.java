package com.cibernet.splatcraft.client.renderer.entity.squid_bumper;

import com.cibernet.splatcraft.client.model.SquidBumperEntityModel;
import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;

public class SquidBumperEntityColorFeatureRenderer extends FeatureRenderer<SquidBumperEntity, SquidBumperEntityModel<SquidBumperEntity>> {
    public SquidBumperEntityColorFeatureRenderer(FeatureRendererContext<SquidBumperEntity, SquidBumperEntityModel<SquidBumperEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, SquidBumperEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        float[] color = ColorUtils.getColorsFromInt(ColorUtils.getInkColor(entity).getColorOrLocked());
        renderModel(this.getContextModel(), SquidBumperEntityRenderer.TEXTURE, matrices, vertices, light, entity, color[0], color[1], color[2]);
    }
}
