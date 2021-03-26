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
        bumper.setPivot(0.0F, 24.0F, 0.0F);
        bumper.setTextureOffset(0, 24).addCuboid(-6.0F, -25.0F, -5.0F, 12.0F, 23.0F, 10.0F, 0.0F, false);
        bumper.setTextureOffset(0, 0).addCuboid(-7.0F, -14.0F, -6.0F, 14.0F, 12.0F, 12.0F, 0.0F, false);

        ears = new ModelPart(this);
        ears.setPivot(0.0F, -17.15F, 0.0F);
        bumper.addChild(ears);
        setAngles(ears, 0.0F, 0.0F, 0.7854F);
        ears.setTextureOffset(44, 40).addCuboid(-9.0F, -9.0F, -1.0F, 16.0F, 16.0F, 2.0F, 0.0F, false);

        base = new ModelPart(this);
        base.setPivot(0.0F, 24.0F, 0.0F);
        base.setTextureOffset(0, 47).addCuboid(-6.0F, -2.0F, -5.0F, 12.0F, 2.0F, 10.0F, 0.0F, false);
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
