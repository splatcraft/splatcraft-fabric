package com.cibernet.splatcraft.client.model.entity;

import com.cibernet.splatcraft.entity.InkProjectileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

public class InkProjectileEntityModel extends SinglePartEntityModel<InkProjectileEntity> {
    private final ModelPart root;

    public InkProjectileEntityModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        data.getRoot().addChild(
            "cube",
            ModelPartBuilder.create()
                .cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 8.0f, 8.0f, false),
            ModelTransform.pivot(0.0F, -8.0F, 0.0F)
        );

        return TexturedModelData.of(data, 16, 16);
    }

    @Override
    public void setAngles(InkProjectileEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
