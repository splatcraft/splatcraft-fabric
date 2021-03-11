package com.cibernet.splatcraft.client.model;

import com.cibernet.splatcraft.entity.InkProjectileEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;

public class InkProjectileEntityModel extends CompositeEntityModel<InkProjectileEntity> {
    private final ModelPart cube;

    public InkProjectileEntityModel() {
        textureWidth = 16;
        textureHeight = 16;

        cube = new ModelPart(this);
        cube.setPivot(0.0F, -8.0F, 0.0F);
        cube.setTextureOffset(0, 0).addCuboid(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, false);
    }

    @Override
    public void setAngles(InkProjectileEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(cube);
    }
}
