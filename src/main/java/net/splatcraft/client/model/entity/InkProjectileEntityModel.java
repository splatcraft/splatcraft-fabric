package net.splatcraft.client.model.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.splatcraft.entity.InkProjectileEntity;

@Environment(EnvType.CLIENT)
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
            ModelTransform.NONE
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
