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
        top.setPivot(0.0F, -0.25F, 0.0F);
        top.setTextureOffset(16, 0).addCuboid(-4, 0, -2, 8, 12, 4, 1);

        tank = new ModelPart(this);
        tank.setPivot(0.0F, -2.25F, -1.225F);
        top.addChild(tank);
        tank.setTextureOffset(0, 19).addCuboid(-2.0F, 3.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        tank.setTextureOffset(16, 19).addCuboid(-2.0F, 10.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        tank.setTextureOffset(0, 24).addCuboid(1.0F, 4.25F, 3.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(6, 24).addCuboid(1.0F, 4.25F, 6.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(10, 24).addCuboid(-2.0F, 4.25F, 6.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(14, 24).addCuboid(-2.0F, 4.25F, 3.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);

        left = new ModelPart(this);
        left.setPivot(0.0F, 0.0F, 0.0F);
        left.setTextureOffset(40, 0).addCuboid(-1, -2, -2, 4, 12, 4, 1);


        right = new ModelPart(this);
        right.setPivot(0.0F, 0.0F, 0.0F);
        right.setTextureOffset(40, 0).addCuboid(-3, -2, -2, 4, 12, 4, 1, true);

        torso = new ModelPart(this);
        torso.addChild(top);
        leftArm.addChild(left);
        rightArm.addChild(right);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25f;

        for (int i = 0; i < 6; i++) {
            ModelPart ink = new ModelPart(this);
            ink.setPivot(0.0F, inkBarY, -0.75F);
            tank.addChild(ink);

            ink.setTextureOffset(115, 0).addCuboid(-1.5F, -13.0F, 4.5F, 3, 1, 3, 0.0F);

            inkPieces.add(ink);
        }
    }
}
