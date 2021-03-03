package com.cibernet.splatcraft.client.model;

import com.cibernet.splatcraft.entity.SquidBumperEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("FieldCanBeLocal")
public class SquidBumperEntityModel extends EntityModel<SquidBumperEntity> {
    private final ModelPart main;
    private final ModelPart bumper;
    private final ModelPart left;
    private final ModelPart right;

    public SquidBumperEntityModel() {
        textureWidth = 128;
        textureHeight = 128;

        main = new ModelPart(this);
        main.setPivot(0.0F, 24.0F, 0.0F);
        main.setTextureOffset(0, 46).addCuboid(-5.0F, -2.0F, -5.0F, 10.0F, 2.0F, 10.0F, 0.0F, false);

        bumper = new ModelPart(this);
        bumper.setPivot(0.0F, 24.0F, 0.0F);
        bumper.setTextureOffset(0, 0).addCuboid(-7.0F, -16.0F, -7.0F, 14.0F, 14.0F, 14.0F, 0.0F, false);
        bumper.setTextureOffset(0, 28).addCuboid(-6.0F, -22.0F, -6.0F, 12.0F, 6.0F, 12.0F, 0.0F, false);
        bumper.setTextureOffset(56, 1).addCuboid(-5.0F, -27.0F, -5.0F, 10.0F, 5.0F, 10.0F, 0.0F, false);
        bumper.setTextureOffset(56, 17).addCuboid(-4.0F, -30.0F, -4.0F, 8.0F, 3.0F, 8.0F, 0.0F, false);

        left = new ModelPart(this);
        left.setPivot(3.3308F, -12.7034F, 0.5F);
        bumper.addChild(left);
        setRotationAngle(left, 0.0F, 0.0F, 0.7854F);
        left.setTextureOffset(72, 28).addCuboid(-11.3308F, -12.0465F, -1.5F, 10.0F, 10.0F, 2.0F, 0.0F, false);

        right = new ModelPart(this);
        right.setPivot(-3.3308F, -12.7034F, 0.5F);
        bumper.addChild(right);
        setRotationAngle(right, 0.0F, 0.0F, -0.7854F);
        right.setTextureOffset(48, 28).addCuboid(1.3261F, -12.0465F, -1.5F, 10.0F, 10.0F, 2.0F, 0.0F, true);
    }

    @Override
    public void setAngles(SquidBumperEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    @Override
    public void animateModel(SquidBumperEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);

        bumper.pivotY = ((float)Math.PI / 180F) * MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadYaw, entity.prevHeadYaw) + (float)Math.PI;

        main.pivotX = 0.0F;
        main.pivotY = 0.0F;
        main.pivotZ = 0.0F;

        float scale = (10 - Math.min(entity.getRespawnTime(), 10))/10f;

        bumper.pivotY = 24;

        if (entity.getInkHealth() <= 0f) {
            bumper.pivotY *= 1 / scale;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        renderBase(matrixStack, buffer, packedLight, packedOverlay, r, g, b, a);
        renderBumper(matrixStack, buffer, packedLight, packedOverlay, r, g, b, a);
    }

    public void render(SquidBumperEntity entity, MatrixStack matrices, VertexConsumer consumer, int light) {
        float scale = (10 - Math.min(entity.getRespawnTime(), 10))/10f;
        int color = entity.getInkColor().getColor();
        float r = (float) (Math.floor(color / (256.0F * 256.0F)) / 255.0F);
        float g = (float) ((Math.floor(color / 256.0F) % 256.0F) / 255.0F);
        float b = (color % 256) / 255f;

        renderBase(matrices, consumer, light, OverlayTexture.DEFAULT_UV, r, g, b, 1);
        matrices.push();
        matrices.translate(0, 24 * (scale > 0 ? 1/scale : 0), 0);
        matrices.scale(scale, scale, scale);
        renderBumper(matrices, consumer, light, OverlayTexture.DEFAULT_UV, r, g, b, 1);
        matrices.pop();

    }


    public void renderBase(MatrixStack matrices, VertexConsumer consumer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        main.render(matrices, consumer, packedLight, packedOverlay, r, g, b, a);
    }

    public void renderBumper(MatrixStack matrices, VertexConsumer consumer, int light, int overlay, float r, float g, float b, float a) {
        bumper.render(matrices, consumer, light, overlay, r, g, b, a);
    }

    public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.pivotX = x;
        modelRenderer.pivotY = y;
        modelRenderer.pivotZ = z;
    }
}
