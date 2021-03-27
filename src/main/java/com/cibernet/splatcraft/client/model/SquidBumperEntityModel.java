package com.cibernet.splatcraft.client.model;

import com.cibernet.splatcraft.entity.SquidBumperEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;

@SuppressWarnings("FieldCanBeLocal")
public class SquidBumperEntityModel<T extends SquidBumperEntity> extends CompositeEntityModel<T> {
    public final ModelPart bumper;
    private final ModelPart ears;
    private final ModelPart base;

    public SquidBumperEntityModel() {
        textureWidth = 80;
        textureHeight = 64;
        bumper = new ModelPart(this);
        bumper.setPivot(0.0f, 24.0f, 0.0f);
        bumper.setTextureOffset(0, 24).addCuboid(-6.0f, -25.0f, -5.0f, 12.0f, 23.0f, 10.0f, 0.0f, false);
        bumper.setTextureOffset(0, 0).addCuboid(-7.0f, -14.0f, -6.0f, 14.0f, 12.0f, 12.0f, 0.0f, false);

        ears = new ModelPart(this);
        ears.setPivot(0.0f, -17.15f, 0.0f);
        bumper.addChild(ears);
        setAngles(ears, 0.0f, 0.0f, 0.7854f);
        ears.setTextureOffset(44, 40).addCuboid(-9.0f, -9.0f, -1.0f, 16.0f, 16.0f, 2.0f, 0.0f, false);

        base = new ModelPart(this);
        base.setPivot(0.0f, 24.0f, 0.0f);
        base.setTextureOffset(0, 47).addCuboid(-6.0f, -2.0f, -5.0f, 12.0f, 2.0f, 10.0f, 0.0f, false);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.bumper.visible = !entity.isFlattened();
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.bumper, this.base);
    }

    public void setAngles(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.pitch = x;
        modelRenderer.yaw = y;
        modelRenderer.roll = z;
    }
}
