package com.cibernet.splatcraft.client.model.ink_tank;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;

@SuppressWarnings({"FieldCanBeLocal","unused"})
public class ClassicInkTankArmorModel extends AbstractInkTankArmorModel {
    private final ModelPart top;
    private final ModelPart tank;

    public ClassicInkTankArmorModel(ModelPart root) {
        super(root);

        this.top = this.body.getChild("top");
        this.tank = this.top.getChild("tank");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        ModelPartData body = root.getChild(EntityModelPartNames.BODY);

        ModelPartData top = body.addChild(
            "top",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-4.75f, -0.25f, -2.5f, 9.0f, 12.0f, 5.0f)
                .uv(30, 0)
                .cuboid(-1.0f, 3.0f, 2.0f, 2.0f, 1.0f, 2.0f),
            ModelTransform.pivot(0.0f, -0.25f, 0.0f)
        );

        ModelPartData tank = top.addChild(
            "tank",
            ModelPartBuilder.create()
                .uv(0, 19)
                .cuboid(-2.0f, 3.25f, 3.25f, 4.0f, 1.0f, 4.0f)
                .uv(20, 28)
                .cuboid(-1.5f, 2.25f, 3.75f, 3.0f, 1.0f, 3.0f)
                .uv(22, 32)
                .cuboid(-2.0f, 2.0f, 4.75f, 4.0f, 2.0f, 1.0f)
                .uv(21, 35)
                .cuboid(-0.5f, 2.0f, 3.25f, 1.0f, 2.0f, 4.0f)
                .uv(16, 19)
                .cuboid(-2.0f, 11.25f, 3.25f, 4.0f, 1.0f, 4.0f)
                .uv(20, 24)
                .cuboid(-1.5f, 11.75f, 3.75f, 3.0f, 1.0f, 3.0f)
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
                .cuboid(-1.0f, 0.75f, 4.25f, 2.0f, 2.0f, 2.0f)
                .uv(8, 34)
                .cuboid(-2.75f, 3.75f, 4.75f, 1.0f, 1.0f, 1.0f),
            ModelTransform.pivot(0.0f, 0.75f, 0.25f)
        );
        for (int i = 0; i < 7; i++) {
            tank.addChild(
                "ink_" + i,
                ModelPartBuilder.create()
                                .uv(52, 0)
                                .cuboid(-1.5f, -12F, 4.5f, 3.0f, 1.0f, 3.0f),
                ModelTransform.pivot(0.0f, inkBarY, -0.75f)
            );

            // TODO inkPieces.add(ink);
        }

        return TexturedModelData.of(data, 128, 128);
    }
}
