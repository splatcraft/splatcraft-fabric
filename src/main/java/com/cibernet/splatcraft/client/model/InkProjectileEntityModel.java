package com.cibernet.splatcraft.client.model;

import com.cibernet.splatcraft.entity.InkProjectileEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class InkProjectileEntityModel extends EntityModel<InkProjectileEntity> {
    private final ModelPart part;

    public InkProjectileEntityModel() {
        textureWidth = 16;
        textureHeight = 16;

        part = new ModelPart(this);
        part.setPivot(0.0F, 24.0F, 0.0F);
        part.setTextureOffset(0, 0).addCuboid(-4F, -24F, -4F, 8.0F, 8.0F, 8.0F, 0.0F, false);
    }

    @Override
    public void setAngles(InkProjectileEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
