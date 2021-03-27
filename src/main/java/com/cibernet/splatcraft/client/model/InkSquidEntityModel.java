package com.cibernet.splatcraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class InkSquidEntityModel extends CompositeEntityModel<LivingEntity> {
    private final ModelPart body;
    private final ModelPart leftTentacle;
    private final ModelPart rightTentacle;

    public InkSquidEntityModel() {
        textureWidth = 48;
        textureHeight = 32;

        body = new ModelPart(this);
        body.setPivot(0.0f, 22.5f, 3.0f);
        body.setTextureOffset(0, 16).addCuboid(-4.0f, -1.5f, -12.0f, 8.0f, 3.0f, 3.0f, 0.0f, false);
        body.setTextureOffset(0, 0).addCuboid(-5.5f, -2.0f, -9.0f, 11.0f, 4.0f, 5.0f, 0.0f, false);
        body.setTextureOffset(0, 9).addCuboid(-3.5f, -1.5f, -4.0f, 7.0f, 3.0f, 4.0f, 0.0f, false);
        body.setTextureOffset(20, 20).addCuboid(-3.0f, -1.5f, 0.0f, 6.0f, 3.0f, 2.0f, 0.0f, false);

        leftTentacle = new ModelPart(this);
        leftTentacle.setPivot(2.5f, 22.5f, 2.0f);
        leftTentacle.setTextureOffset(10, 24).addCuboid(-1.0f, -1.0f, 1.0f, 2.0f, 2.0f, 4.0f, 0.0f, true);
        leftTentacle.setTextureOffset(0, 22).addCuboid(-2.0f, -1.0f, 5.0f, 3.0f, 2.0f, 4.0f, 0.0f, true);

        rightTentacle = new ModelPart(this);
        rightTentacle.setPivot(-2.5f, 22.5f, 2.0f);
        rightTentacle.setTextureOffset(10, 24).addCuboid(-1.0f, -1.0f, 1.0f, 2.0f, 2.0f, 4.0f, 0.0f, false);
        rightTentacle.setTextureOffset(0, 22).addCuboid(-1.0f, -1.0f, 5.0f, 3.0f, 2.0f, 4.0f, 0.0f, false);
    }

    @Override
    public void setAngles(LivingEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    @Override
    public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        boolean isSwimming = entity.isSwimming();

        if (!entity.hasVehicle()) {
            float angle = isSwimming ? (float) -((entity.pitch * Math.PI) / 180F) : (float) (entity.getY() - entity.prevY) * 1.1f;
            this.body.pitch = (float) -Math.min(Math.PI / 2, Math.max(-Math.PI / 2, angle));

            this.leftTentacle.pitch = this.body.pitch / 2;
            this.rightTentacle.pitch = this.body.pitch / 2;
        }

        this.leftTentacle.yaw = MathHelper.cos(limbAngle * 0.6662f + (float) Math.PI) * 1.4f * limbDistance / (isSwimming ? 2.2f : 1.5f);
        this.rightTentacle.yaw = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance / (isSwimming ? 2.2f : 1.5f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(body, leftTentacle, rightTentacle);
    }
}
