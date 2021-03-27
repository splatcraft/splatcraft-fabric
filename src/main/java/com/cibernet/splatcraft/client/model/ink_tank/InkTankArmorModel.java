package com.cibernet.splatcraft.client.model.ink_tank;

import net.minecraft.client.model.ModelPart;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class InkTankArmorModel extends AbstractInkTankArmorModel {
    private final ModelPart top;
    private final ModelPart middle;
    private final ModelPart tank;

    public InkTankArmorModel() {
        textureWidth = 128;
        textureHeight = 128;

        top = new ModelPart(this);
        top.setPivot(0.0f, 0.0f, 0.0f);
        top.setTextureOffset(0, 112).addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 0.0f, false);

        middle = new ModelPart(this);
        middle.setPivot(0.0f, -0.25f, 0.0f);
        middle.setTextureOffset(0, 0).addCuboid(-4.75f, -0.25f, -2.5f, 9.0f, 12.0f, 5.0f, 0.0f, false);
        middle.setTextureOffset(31, 0).addCuboid(-1.0f, 3.0f, 2.5f, 2.0f, 1.0f, 1.0f, 0.0f, false);

        tank = new ModelPart(this);
        tank.setPivot(0.0f, 0.75f, 0.75f);
        middle.addChild(tank);
        tank.setTextureOffset(31, 2).addCuboid(-0.5f, 1.75f, 2.0f, 1.0f, 2.0f, 2.0f, 0.0f, false);
        tank.setTextureOffset(0, 19).addCuboid(-2.0f, 3.25f, 3.25f, 4.0f, 1.0f, 4.0f, 0.0f, false);
        tank.setTextureOffset(16, 19).addCuboid(-2.0f, 11.25f, 3.25f, 4.0f, 1.0f, 4.0f, 0.0f, false);
        tank.setTextureOffset(0, 24).addCuboid(1.0f, 4.25f, 3.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(6, 24).addCuboid(1.0f, 4.25f, 6.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(10, 24).addCuboid(-2.0f, 4.25f, 6.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(14, 24).addCuboid(-2.0f, 4.25f, 3.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0f, 9.25f, 6.25f, 1.0f, 1.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0f, 7.25f, 6.25f, 1.0f, 1.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0f, 5.25f, 6.25f, 1.0f, 1.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(0, 33).addCuboid(-1.0f, 2.25f, 4.25f, 2.0f, 1.0f, 2.0f, 0.0f, false);
        tank.setTextureOffset(8, 33).addCuboid(-3.5f, 2.5f, 4.25f, 2.0f, 1.0f, 2.0f, 0.0f, false);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25f;

        for (int i = 0; i < 7; i++) {
            ModelPart ink = new ModelPart(this);
            ink.setPivot(0.0f, inkBarY, -0.75f);
            tank.addChild(ink);

            ink.setTextureOffset(116, 30).addCuboid(-1.5f, -12.0f, 4.5f, 3, 1, 3, 0);

            inkPieces.add(ink);
        }

        torso = new ModelPart(this);
        torso.addChild(middle);
    }
}
