package com.cibernet.splatcraft.client.model.ink_tank;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;

@SuppressWarnings({"FieldCanBeLocal","unused"})
public class InkTankJrArmorModel extends AbstractInkTankArmorModel {
    private final ModelPart top;
    private final ModelPart tank;
    private final ModelPart tag;

    public InkTankJrArmorModel(ModelPart root) {
        super(root);

        this.top = root.getChild("top");
        this.tank = this.top.getChild("tank");
        this.tag = this.top.getChild("tag");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        ModelPartData top = root.getChild(EntityModelPartNames.BODY).addChild(
            "top",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-4.8056f, -0.25f, -2.5f, 9.0f, 12.0f, 5.0f)
                .uv(31, 0)
                .cuboid(-1.0f, 3.0f, 2.5f, 2.0f, 1.0f, 1.0f),
            ModelTransform.pivot(0.0f, -0.25f, 0.0f)
        );

        ModelPartData tank = top.addChild(
            "tank",
            ModelPartBuilder.create()
                .uv(20, 18)
                .cuboid(-2.0f, 1.5f, 3.75f, 4.0f, 2.0f, 4.0f)
                .uv(8, 33)
                .cuboid(-3.5f, 3.15f, 4.25f, 2.0f, 1.0f, 2.0f)
                .uv(12, 39)
                .cuboid(0.9875f, 5.25f, 7.25f, 1.0f, 1.0f, 1.0f)
                .uv(12, 39)
                .cuboid(0.9875f, 7.25f, 7.25f, 1.0f, 1.0f, 1.0f)
                .uv(12, 39)
                .cuboid(0.9875f, 9.25f, 7.25f, 1.0f, 1.0f, 1.0f)
                .uv(14, 24)
                .cuboid(-3.0f, 4.25f, 3.25f, 1.0f, 7.0f, 1.0f)
                .uv(10, 24)
                .cuboid(-3.0f, 4.25f, 7.25f, 1.0f, 7.0f, 1.0f)
                .uv(6, 24)
                .cuboid(2.0f, 4.25f, 7.25f, 1.0f, 7.0f, 1.0f)
                .uv(0, 24)
                .cuboid(2.0f, 4.25f, 3.25f, 1.0f, 7.0f, 1.0f)
                .uv(18, 25)
                .cuboid(-3.0f, 11.25f, 3.25f, 6.0f, 1.0f, 5.0f)
                .uv(0, 18)
                .cuboid(-2.5f, 3.25f, 3.25f, 5.0f, 1.0f, 5.0f)
                .uv(31, 2)
                .cuboid(-0.5f, 1.75f, 2.0f, 1.0f, 2.0f, 2.0f),
            ModelTransform.pivot(0.0f, 0.75f, 0.75f)
        );
        for (int i = 0; i < 7; i++) {
            tank.addChild(
                "ink_" + i,
                ModelPartBuilder.create()
                    .uv(110, 0)
                    .cuboid(-2.5f, -12.0f, 4.5f, 5.0f, 1.0f, 4.0f),
                ModelTransform.pivot(0.0f, inkBarY, -0.75f)
            );

            // TODO inkPieces.add(ink);
        }

        top.addChild(
            "tag",
            ModelPartBuilder.create()
                .uv(8, 63)
                .cuboid(-0.8541f, 0.6055f, -2.1381f, 2, 0, 2),
            ModelTransform.of(-3.1168f, 2.8445f, 8.9821f, -0.1309f, -0.3927f, -0.3054f)
        );

        return TexturedModelData.of(data, 128, 128);
    }
}
