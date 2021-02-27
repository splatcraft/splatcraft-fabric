package com.cibernet.splatcraft.client.renderer.entity.ink_squid;

import com.cibernet.splatcraft.client.model.InkSquidEntityModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class PlayerEntityInkSquidRenderer extends InkSquidEntityRenderer {
    public PlayerEntityInkSquidRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher, null);
    }

    @Override
    public void render(LivingEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumers, int light) {
        matrices.push();
        this.model.handSwingProgress = this.getHandSwingProgress(entity, tickDelta);

        this.model.child = entity.isBaby();
        float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
        float headYaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadYaw, entity.headYaw);
        float headBodyYawDelta = headYaw - bodyYaw;
        if (entity.getVehicle() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity.getVehicle();
            bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
            headBodyYawDelta = headYaw - bodyYaw;
            float rotationWrapped = MathHelper.wrapDegrees(headBodyYawDelta);
            if (rotationWrapped < -85.0F) {
                rotationWrapped = -85.0F;
            }

            if (rotationWrapped >= 85.0F) {
                rotationWrapped = 85.0F;
            }

            bodyYaw = headYaw - rotationWrapped;
            if (rotationWrapped * rotationWrapped > 2500.0F) {
                bodyYaw += rotationWrapped * 0.2F;
            }

            headBodyYawDelta = headYaw - bodyYaw;
        }

        float pitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch);
        if (entity.getPose() == EntityPose.SLEEPING) {
            Direction direction = entity.getSleepingDirection();
            if (direction != null) {
                float f4 = entity.getEyeHeight(EntityPose.STANDING) - 0.1F;
                matrices.translate((float) -direction.getOffsetX() * f4, 0.0D, (float) -direction.getOffsetZ() * f4);
            }
        }

        float animationProgress = this.getAnimationProgress(entity, tickDelta);
        this.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
        matrices.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, matrices, tickDelta);
        matrices.translate(0.0D, -1.501F, 0.0D);
        float limbDistance = 0.0F;
        float limbDistanceDelta = 0.0F;
        if (entity.isAlive()) {
            limbDistance = MathHelper.lerp(tickDelta, entity.lastLimbDistance, entity.limbDistance);
            limbDistanceDelta = entity.limbAngle - entity.limbDistance * (1.0F - tickDelta);
            if (entity.isBaby()) {
                limbDistanceDelta *= 3.0F;
            }

            if (limbDistance > 1.0F) {
                limbDistance = 1.0F;
            }
        }

        this.model.animateModel(entity, limbDistanceDelta, limbDistance, tickDelta);
        this.model.setAngles(entity, limbDistanceDelta, limbDistance, animationProgress, headBodyYawDelta, pitch);
        MinecraftClient minecraft = MinecraftClient.getInstance();
        boolean isVisible = this.isVisible(entity);
        boolean isNotInvisible = !isVisible && !entity.isInvisibleTo(minecraft.player);
        boolean hasOutline = minecraft.hasOutline(entity);
        RenderLayer rendertype = this.getRenderLayer(entity, isVisible, isNotInvisible, hasOutline);
        if (rendertype != null) {
            VertexConsumer vertices = consumers.getBuffer(rendertype);
            int overlay = PlayerEntityInkSquidRenderer.getOverlay(entity, this.getAnimationProgress(entity, tickDelta));
            this.model.render(matrices, vertices, light, overlay, 1.0F, 1.0F, 1.0F, isNotInvisible ? 0.15F : 1.0F);
        }

        if (!entity.isSpectator()) {
            for(FeatureRenderer<LivingEntity, InkSquidEntityModel> layerrenderer : this.features) {
                layerrenderer.render(matrices, consumers, light, entity, limbDistanceDelta, limbDistance, tickDelta, animationProgress, headBodyYawDelta, pitch);
            }
        }

        matrices.pop();
    }
}
