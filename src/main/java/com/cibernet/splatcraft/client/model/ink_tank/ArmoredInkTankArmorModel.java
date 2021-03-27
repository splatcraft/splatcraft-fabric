package com.cibernet.splatcraft.client.model.ink_tank;

import net.minecraft.client.model.ModelPart;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class ArmoredInkTankArmorModel extends AbstractInkTankArmorModel {
    private final ModelPart top;
    private final ModelPart tank;
    private final ModelPart left;
    private final ModelPart right;

    public ArmoredInkTankArmorModel() {
        textureWidth = 128;
        textureHeight = 128;

        top = new ModelPart(this);
        top.setPivot(0.0f, -0.25f, 0.0f);
        top.setTextureOffset(16, 0).addCuboid(-4, 0, -2, 8, 12, 4, 1);

        tank = new ModelPart(this);
        tank.setPivot(0.0f, -2.25f, -1.225f);
        top.addChild(tank);
        tank.setTextureOffset(0, 19).addCuboid(-2.0f, 3.25f, 3.25f, 4.0f, 1.0f, 4.0f, 0.0f, false);
        tank.setTextureOffset(16, 19).addCuboid(-2.0f, 10.25f, 3.25f, 4.0f, 1.0f, 4.0f, 0.0f, false);
        tank.setTextureOffset(0, 24).addCuboid(1.0f, 4.25f, 3.25f, 1.0f, 6.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(6, 24).addCuboid(1.0f, 4.25f, 6.25f, 1.0f, 6.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(10, 24).addCuboid(-2.0f, 4.25f, 6.25f, 1.0f, 6.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(14, 24).addCuboid(-2.0f, 4.25f, 3.25f, 1.0f, 6.0f, 1.0f, 0.0f, false);

        left = new ModelPart(this);
        left.setPivot(0.0f, 0.0f, 0.0f);
        left.setTextureOffset(40, 0).addCuboid(-1, -2, -2, 4, 12, 4, 1);


        right = new ModelPart(this);
        right.setPivot(0.0f, 0.0f, 0.0f);
        right.setTextureOffset(40, 0).addCuboid(-3, -2, -2, 4, 12, 4, 1, true);

        torso = new ModelPart(this);
        torso.addChild(top);
        leftArm.addChild(left);
        rightArm.addChild(right);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25f;

        for (int i = 0; i < 6; i++) {
            ModelPart ink = new ModelPart(this);
            ink.setPivot(0.0f, inkBarY, -0.75f);
            tank.addChild(ink);

            ink.setTextureOffset(115, 0).addCuboid(-1.5f, -13.0f, 4.5f, 3, 1, 3, 0.0f);

            inkPieces.add(ink);
        }
    }
}
