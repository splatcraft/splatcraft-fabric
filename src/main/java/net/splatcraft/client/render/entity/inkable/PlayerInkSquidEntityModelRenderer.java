package net.splatcraft.client.render.entity.inkable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.splatcraft.client.model.inkable.InkSquidEntityModel;

@Environment(EnvType.CLIENT)
public class PlayerInkSquidEntityModelRenderer extends InkSquidEntityModelRenderer<AbstractClientPlayerEntity> {
    public PlayerInkSquidEntityModelRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(AbstractClientPlayerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        matrices.push();
        this.model.handSwingProgress = this.getHandSwingProgress(entity, tickDelta);

        this.model.child = entity.isBaby();
        float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
        float headYaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadYaw, entity.headYaw);
        float headBodyYawDelta = headYaw - bodyYaw;
        if (entity.getVehicle() instanceof LivingEntity livingEntity) {
            bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
            headBodyYawDelta = headYaw - bodyYaw;
            float rotationWrapped = MathHelper.wrapDegrees(headBodyYawDelta);
            if (rotationWrapped < -85.0f) {
                rotationWrapped = -85.0f;
            }

            if (rotationWrapped >= 85.0f) {
                rotationWrapped = 85.0f;
            }

            bodyYaw = headYaw - rotationWrapped;
            if (rotationWrapped * rotationWrapped > 2500.0f) {
                bodyYaw += rotationWrapped * 0.2f;
            }

            headBodyYawDelta = headYaw - bodyYaw;
        }

        float pitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
        if (entity.getPose() == EntityPose.SLEEPING) {
            Direction direction = entity.getSleepingDirection();
            if (direction != null) {
                float f4 = entity.getEyeHeight(EntityPose.STANDING) - 0.1f;
                matrices.translate((float) -direction.getOffsetX() * f4, 0.0d, (float) -direction.getOffsetZ() * f4);
            }
        }

        float animationProgress = this.getAnimationProgress(entity, tickDelta);
        this.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
        matrices.scale(-1.0f, -1.0f, 1.0f);
        this.scale(entity, matrices, tickDelta);
        matrices.translate(0.0d, -1.501f, 0.0d);
        float limbDistance = 0.0f;
        float limbDistanceDelta = 0.0f;
        if (entity.isAlive()) {
            limbDistance = MathHelper.lerp(tickDelta, entity.lastLimbDistance, entity.limbDistance);
            limbDistanceDelta = entity.limbAngle - entity.limbDistance * (1.0f - tickDelta);
            if (entity.isBaby()) {
                limbDistanceDelta *= 3.0f;
            }

            if (limbDistance > 1.0f) {
                limbDistance = 1.0f;
            }
        }

        this.model.animateModel(entity, limbDistanceDelta, limbDistance, tickDelta);
        this.model.setAngles(entity, limbDistanceDelta, limbDistance, animationProgress, headBodyYawDelta, pitch);
        boolean isTranslucent = entity.isSpectator() || entity.isInvisibleTo(MinecraftClient.getInstance().player);
        RenderLayer renderLayer = isTranslucent ? RenderLayer.getEntityTranslucentCull(this.getTexture(entity)) : this.model.getLayer(this.getTexture(entity));
        if (renderLayer != null) {
            this.model.render(matrices, vertices.getBuffer(renderLayer), light, LivingEntityRenderer.getOverlay(entity, this.getAnimationCounter(entity, tickDelta)), 1.0f, 1.0f, 1.0f, isTranslucent ? 0.25f : 1.0f);
        }

        if (!isTranslucent) {
            for (FeatureRenderer<AbstractClientPlayerEntity, InkSquidEntityModel<AbstractClientPlayerEntity>> featureRenderer : this.features) {
                featureRenderer.render(matrices, vertices, light, entity, limbDistanceDelta, limbDistance, tickDelta, animationProgress, headBodyYawDelta, pitch);
            }
        }

        matrices.pop();
    }
}
