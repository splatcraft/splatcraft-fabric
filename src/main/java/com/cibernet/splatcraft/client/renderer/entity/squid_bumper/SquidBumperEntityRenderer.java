package com.cibernet.splatcraft.client.renderer.entity.squid_bumper;

import com.cibernet.splatcraft.client.model.SquidBumperEntityModel;
import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class SquidBumperEntityRenderer extends LivingEntityRenderer<SquidBumperEntity, SquidBumperEntityModel> {
    @SuppressWarnings("unused")
    public SquidBumperEntityRenderer(EntityRenderDispatcher dispatcher, @Nullable EntityRendererRegistry.Context ctx) {
        super(dispatcher, new SquidBumperEntityModel(), 0.5f);
        this.addFeature(new SquidBumperEntityColorFeatureRenderer(this));
    }

    @Override
    protected boolean hasLabel(SquidBumperEntity entity) {
        return !entity.hasCustomName() && !(entity.getInkHealth() >= 20) || super.hasLabel(entity) && (entity.isCustomNameVisible() || entity == this.dispatcher.targetedEntity);
    }
	
	/*
	@Override
	public void render(SquidBumperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumers bufferIn, int packedLightIn)
	{
		getEntityModel().render(entityIn, matrixStackIn, bufferIn.getBuffer(getEntityModel().getRenderLayer(TEXTURE)), packedLightIn);
	}
	*/
    
    @Override
    public void render(SquidBumperEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumer, int light) {
        matrices.push();
        this.model.handSwingProgress = this.getHandSwingProgress(entity, tickDelta);

        boolean shouldRide = entity.hasVehicle() && entity.getPrimaryPassenger() != null;
        this.model.riding = shouldRide;
        this.model.child = entity.isBaby();
        float f = MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
        float f1 = MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadYaw, entity.headYaw);
        float f2 = f1 - f;
        if (shouldRide && entity.getPrimaryPassenger() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity.getPrimaryPassenger();
            f = MathHelper.lerpAngleDegrees(tickDelta, livingentity.prevBodyYaw, livingentity.bodyYaw);
            f2 = f1 - f;
            float f3 = MathHelper.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }

        float f6 = MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch);
        if (entity.getPose() == EntityPose.SLEEPING) {
            Direction direction = entity.getSleepingDirection();
            if (direction != null) {
                float f4 = entity.getEyeHeight(EntityPose.STANDING) - 0.1F;
                matrices.translate((float) -direction.getOffsetX() * f4, 0.0D, (float) -direction.getOffsetZ() * f4);
            }
        }

        float f7 = this.getHandSwingProgress(entity, tickDelta);
        this.setupTransforms(entity, matrices, f7, f, tickDelta);
        matrices.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, matrices, tickDelta);
        matrices.translate(0.0D, -1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldRide && entity.isAlive()) {
            f8 = MathHelper.lerp(tickDelta, entity.lastLimbDistance, entity.limbDistance);
            f5 = entity.limbAngle - entity.limbDistance * (1.0F - tickDelta);
            if (entity.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        this.model.animateModel(entity, f5, f8, tickDelta);
        this.model.setAngles(entity, f5, f8, f7, f2, f6);
        MinecraftClient minecraft = MinecraftClient.getInstance();
        boolean flag = this.isVisible(entity);
        boolean flag1 = !flag && !entity.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.hasOutline(entity);
        RenderLayer rendertype = this.getRenderLayer(entity, flag, flag1, flag2);
        if (rendertype != null) {
            VertexConsumer bufferConsumer = consumer.getBuffer(rendertype);
            int i = this.getLight(entity, this.getAnimationProgress(entity, tickDelta));


            this.model.renderBase(matrices, bufferConsumer, light, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);

            float scale = entity.getInkHealth() <= 0 ? (10 - Math.min(entity.getRespawnTime(), 10))/10f : 1;
            matrices.push();
            matrices.scale(scale, scale, scale);
            this.model.renderBumper(matrices, bufferConsumer, light, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
            matrices.pop();

        }

        if (!entity.isSpectator()) {
            for(FeatureRenderer<SquidBumperEntity, SquidBumperEntityModel> layerrenderer : this.features) {
                layerrenderer.render(matrices, consumer, light, entity, f5, f8, tickDelta, f7, f2, f6);
            }
        }

        matrices.pop();
    }

    @Override
    protected void renderLabelIfPresent(SquidBumperEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.hasCustomName()) {
            super.renderLabelIfPresent(entity, text, matrices, vertexConsumers, light);
        } else {
            float health = 20 - entity.getInkHealth();
            super.renderLabelIfPresent(entity, new LiteralText((health >= 20 ? Formatting.DARK_RED : "") + String.format("%.1f",health)), matrices, vertexConsumers, light);
        }
    }

    @Override
    protected void setupTransforms(SquidBumperEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        //matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
        float punchTime = (float)(entity.world.getTime() - entity.punchCooldown) + tickDelta;
        float hurtTime = (float)(entity.world.getTime() - entity.hurtCooldown) + tickDelta;


        if (punchTime < 5.0F) {
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.sin(punchTime / 1.5F * (float) Math.PI) * 3.0F));
        }
        if (hurtTime < 5.0F) {
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(hurtTime / 1.5F * (float) Math.PI) * 3.0F));
        }
    }

    @Override
    public Identifier getTexture(SquidBumperEntity entity) {
        return SplatcraftEntities.texture("squid_bumper/squid_bumper_overlay");
    }
}
