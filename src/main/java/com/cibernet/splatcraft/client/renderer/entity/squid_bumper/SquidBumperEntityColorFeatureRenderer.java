package com.cibernet.splatcraft.client.renderer.entity.squid_bumper;

import com.cibernet.splatcraft.client.model.SquidBumperEntityModel;
import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SquidBumperEntityColorFeatureRenderer extends FeatureRenderer<SquidBumperEntity, SquidBumperEntityModel> {
    private final SquidBumperEntityModel MODEL = new SquidBumperEntityModel();

    public SquidBumperEntityColorFeatureRenderer(FeatureRendererContext<SquidBumperEntity, SquidBumperEntityModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider consumers, int light, SquidBumperEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        int color = ColorUtils.getInkColor(entity).getColor();
        /*if (SplatcraftConfig.COLORS.colorLock.value) {
            color = ColorUtils.getLockedColor(color);
        } TODO */
        float r = ((color & 16711680) >> 16) / 255.0f;
        float g = ((color & '\uff00') >> 8) / 255.0f;
        float b = (color & 255) / 255.0f;

        renderCopyCutoutModel(this.getContextModel(), MODEL, SplatcraftEntities.texture(SquidBumperEntity.id + "/" + SquidBumperEntity.id), matrices, consumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch, r, g, b);
    }

    protected static <T extends SquidBumperEntity> void renderCopyCutoutModel(SquidBumperEntityModel modelParent, SquidBumperEntityModel model, Identifier texture, MatrixStack matrices, VertexConsumerProvider consumers, int light, T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta, float r, float g, float b) {
        if (!entity.isInvisible()) {
            modelParent.copyStateTo(model);
            model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            renderCutoutModel(model, texture, matrices, consumers, light, entity, r, g, b);
        }
    }

    protected static void renderCutoutModel(SquidBumperEntityModel model, Identifier texture, MatrixStack matrices, VertexConsumerProvider consumers, int light, SquidBumperEntity entity, float r, float g, float b) {
        VertexConsumer ivertexbuilder = consumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
        model.renderBase(matrices, ivertexbuilder, light, LivingEntityRenderer.getOverlay(entity, 0.0F), r, g, b, 1.0F);

        float scale = entity.getInkHealth() <= 0 ? (10 - Math.min(entity.getRespawnTime(), 10))/10f : 1;
        matrices.push();
        matrices.scale(scale, scale, scale);
        model.renderBumper(matrices, ivertexbuilder, light, LivingEntityRenderer.getOverlay(entity, 0.0F), r, g, b, 1.0F);
        matrices.pop();
    }
}
