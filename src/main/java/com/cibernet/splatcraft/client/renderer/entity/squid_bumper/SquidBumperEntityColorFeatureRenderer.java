package com.cibernet.splatcraft.client.renderer.entity.squid_bumper;

import com.cibernet.splatcraft.client.model.entity.SquidBumperEntityModel;
import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;

public class SquidBumperEntityColorFeatureRenderer<T extends SquidBumperEntity> extends FeatureRenderer<T, SquidBumperEntityModel<T>> {
    public SquidBumperEntityColorFeatureRenderer(FeatureRendererContext<T, SquidBumperEntityModel<T>> ctx) {
        super(ctx);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        float[] color = ColorUtil.getInkColor(entity).getColorOrLockedComponents();
        renderModel(this.getContextModel(), SquidBumperEntityRenderer.TEXTURE, matrices, vertices, light, entity, color[0], color[1], color[2]);
    }
}
