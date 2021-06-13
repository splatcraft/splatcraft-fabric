package com.cibernet.splatcraft.client.model.ink_tank;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;

@SuppressWarnings({"FieldCanBeLocal","unused"})
public class InkTankArmorModel extends AbstractInkTankArmorModel {
    private final ModelPart top;
    private final ModelPart middle;
    private final ModelPart tank;

    public InkTankArmorModel(ModelPart root) {
        super(root);

        this.top = root.getChild("top");
        this.middle = root.getChild("middle");
        this.tank = this.middle.getChild("tank");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        root.addChild(
            "top",
            ModelPartBuilder.create()
                 .uv(0, 112)
                .cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f),
            ModelTransform.NONE
        );

        ModelPartData middle = root.getChild(EntityModelPartNames.BODY).addChild(
            "middle",
            ModelPartBuilder.create()
                 .uv(0, 0)
                 .cuboid(-4.75f, -0.25f, -2.5f, 9.0f, 12.0f, 5.0f)
                 .uv(31, 0)
                 .cuboid(-1.0f, 3.0f, 2.5f, 2.0f, 1.0f, 1.0f),
            ModelTransform.pivot(0.0f, -0.25f, 0.0f)
        );

        ModelPartData tank = middle.addChild(
            "tank",
            ModelPartBuilder.create()
                .uv(31, 2)
                .cuboid(-0.5f, 1.75f, 2.0f, 1.0f, 2.0f, 2.0f)
                .uv(0, 19)
                .cuboid(-2.0f, 3.25f, 3.25f, 4.0f, 1.0f, 4.0f)
                .uv(16, 19)
                .cuboid(-2.0f, 11.25f, 3.25f, 4.0f, 1.0f, 4.0f)
                .uv(0, 24)
                .cuboid(1.0f, 4.25f, 3.25f, 1.0f, 7.0f, 1.0f)
                .uv(6, 24)
                .cuboid(1.0f, 4.25f, 6.25f, 1.0f, 7.0f, 1.0f)
                .uv(10, 24)
                .cuboid(-2.0f, 4.25f, 6.25f, 1.0f, 7.0f, 1.0f)
                .uv(14, 24)
                .cuboid(-2.0f, 4.25f, 3.25f, 1.0f, 7.0f, 1.0f)
                .uv(12, 39)
                .cuboid(0.0f, 9.25f, 6.25f, 1.0f, 1.0f, 1.0f)
                .uv(12, 39)
                .cuboid(0.0f, 7.25f, 6.25f, 1.0f, 1.0f, 1.0f)
                .uv(12, 39)
                .cuboid(0.0f, 5.25f, 6.25f, 1.0f, 1.0f, 1.0f)
                .uv(0, 33)
                .cuboid(-1.0f, 2.25f, 4.25f, 2.0f, 1.0f, 2.0f)
                .uv(8, 33)
                .cuboid(-3.5f, 2.5f, 4.25f, 2.0f, 1.0f, 2.0f),
            ModelTransform.pivot(0.0f, 0.75f, 0.75f)
        );
        for (int i = 0; i < 7; i++) {
            tank.addChild(
                "ink_" + i,
                ModelPartBuilder.create()
                    .uv(116, 30)
                    .cuboid(-1.5f, -12.0f, 4.5f, 3.0f, 1.0f, 3.0f),
                ModelTransform.pivot(0.0f, inkBarY, -0.75f)
            );

            // TODO inkPieces.add(ink);
        }

        return TexturedModelData.of(data, 128, 128);
    }
}
