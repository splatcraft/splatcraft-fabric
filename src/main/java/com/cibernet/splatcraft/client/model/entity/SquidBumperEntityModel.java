package com.cibernet.splatcraft.client.model.entity;

import com.cibernet.splatcraft.entity.SquidBumperEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

@SuppressWarnings("FieldCanBeLocal")
public class SquidBumperEntityModel<T extends SquidBumperEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart bumper;
    private final ModelPart ears;

    public SquidBumperEntityModel(ModelPart root) {
        this.root = root;
        this.base = root.getChild("base");
        this.bumper = root.getChild("bumper");
        this.ears = bumper.getChild("ears");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        root.addChild(
            "base",
            ModelPartBuilder.create()
                .uv(0, 47)
                .cuboid(-6.0f, -2.0f, -5.0f, 12.0f, 2.0f, 10.0f),
            ModelTransform.pivot(0.0f, 24.0f, 0.0f)
        );

        ModelPartData bumper = root.addChild(
            "bumper",
            ModelPartBuilder.create()
                .uv(0, 24)
                .cuboid(-6.0f, -25.0f, -5.0f, 12.0f, 23.0f, 10.0f)
                .uv(0, 0)
                .cuboid(-7.0f, -14.0f, -6.0f, 14.0f, 12.0f, 12.0f),
            ModelTransform.pivot(0.0f, 24.0f, 0.0f)
        );
        bumper.addChild(
            "ears",
            ModelPartBuilder.create()
                .uv(44, 40)
                .cuboid(-9.0f, -9.0f, -1.0f, 16.0f, 16.0f, 2.0f),
            ModelTransform.of(0.0f, -17.15f, 0.0f, 0.0f, 0.0f, 0.7854f)
        );

        return TexturedModelData.of(data, 80, 64);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.bumper.visible = !entity.isFlattened();
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    public void setAngles(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.pitch = x;
        modelRenderer.yaw = y;
        modelRenderer.roll = z;
    }
}
