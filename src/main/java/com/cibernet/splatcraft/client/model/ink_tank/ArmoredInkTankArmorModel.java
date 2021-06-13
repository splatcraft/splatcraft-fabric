package com.cibernet.splatcraft.client.model.ink_tank;

import net.minecraft.client.model.*;

@SuppressWarnings("FieldCanBeLocal")
public class ArmoredInkTankArmorModel extends AbstractInkTankArmorModel {
    private final ModelPart top;
    private final ModelPart tank;
    private final ModelPart left;
    private final ModelPart right;

    public ArmoredInkTankArmorModel(ModelPart root) {
        super(root);

        this.top = root.getChild("top");
        this.tank = root.getChild("tank");
        this.left = root.getChild("left");
        this.right = root.getChild("right");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        ModelPartData top = root.addChild(
            "top",
            ModelPartBuilder.create()
                .uv(16, 0)
                .cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, new Dilation(1.0f)),
            ModelTransform.pivot(0.0f, -0.25f, 0.0f)
        );

        ModelPartData tank = top.addChild(
            "tank",
            ModelPartBuilder.create()
                .uv(0, 19)
                .cuboid(-2.0f, 3.25f, 3.25f, 4.0f, 1.0f, 4.0f)
                .uv(16, 19)
                .cuboid(-2.0f, 10.25f, 3.25f, 4.0f, 1.0f, 4.0f)
                .uv(0, 24)
                .cuboid(1.0f, 4.25f, 3.25f, 1.0f, 6.0f, 1.0f)
                .uv(6, 24)
                .cuboid(1.0f, 4.25f, 6.25f, 1.0f, 6.0f, 1.0f)
                .uv(10, 24)
                .cuboid(-2.0f, 4.25f, 6.25f, 1.0f, 6.0f, 1.0f)
                .uv(14, 24)
                .cuboid(-2.0f, 4.25f, 3.25f, 1.0f, 6.0f, 1.0f),
            ModelTransform.pivot(0.0f, -2.25f, -1.225f)
        );
        for (int i = 0; i < 6; i++) {
            tank.addChild(
                "ink_" + i,
                ModelPartBuilder.create()
                    .uv(115, 0)
                    .cuboid(-1.5f, -13.0f, 4.5f, 3.0f, 1.0f, 3.0f),
                ModelTransform.pivot(0.0f, inkBarY, -0.75f)
            );

            // TODO inkPieces.add(ink);
        }

        ModelPartData body = root.addChild("body", ModelPartBuilder.create(), ModelTransform.NONE);
        body.addChild(
            "left_arm",
            ModelPartBuilder.create()
                .uv(40, 0)
                .cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, new Dilation(1.0f)),
            ModelTransform.NONE
        );
        body.addChild(
            "right_arm",
            ModelPartBuilder.create()
                .uv(40, 0)
                .mirrored()
                .cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, new Dilation(1.0f)),
            ModelTransform.NONE
        );

        return TexturedModelData.of(data, 128, 128);
    }
}
