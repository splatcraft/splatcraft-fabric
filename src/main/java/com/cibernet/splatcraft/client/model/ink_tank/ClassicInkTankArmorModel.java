package com.cibernet.splatcraft.client.model.ink_tank;

import net.minecraft.client.model.ModelPart;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class ClassicInkTankArmorModel extends AbstractInkTankArmorModel {
    private final ModelPart top;
    private final ModelPart tank;

    public ClassicInkTankArmorModel() {
        textureWidth = 128;
        textureHeight = 128;

        top = new ModelPart(this);
        top.setPivot(0.0f, -0.25f, 0.0f);
        top.setTextureOffset(0, 0).addCuboid(-4.75f, -0.25f, -2.5f, 9.0f, 12.0f, 5.0f, 0.0f, false);
        top.setTextureOffset(30, 0).addCuboid(-1.0f, 3.0f, 2.0f, 2.0f, 1.0f, 2.0f, 0.0f, false);

        tank = new ModelPart(this);
        tank.setPivot(0.0f, 0.75f, 0.25f);
        top.addChild(tank);
        tank.setTextureOffset(0, 19).addCuboid(-2.0f, 3.25f, 3.25f, 4.0f, 1.0f, 4.0f, 0.0f, false);
        tank.setTextureOffset(20, 28).addCuboid(-1.5f, 2.25f, 3.75f, 3.0f, 1.0f, 3.0f, 0.0f, false);
        tank.setTextureOffset(22, 32).addCuboid(-2.0f, 2.0f, 4.75f, 4.0f, 2.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(21, 35).addCuboid(-0.5f, 2.0f, 3.25f, 1.0f, 2.0f, 4.0f, 0.0f, false);
        tank.setTextureOffset(16, 19).addCuboid(-2.0f, 11.25f, 3.25f, 4.0f, 1.0f, 4.0f, 0.0f, false);
        tank.setTextureOffset(20, 24).addCuboid(-1.5f, 11.75f, 3.75f, 3.0f, 1.0f, 3.0f, 0.0f, false);
        tank.setTextureOffset(0, 24).addCuboid(1.0f, 4.25f, 3.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(6, 24).addCuboid(1.0f, 4.25f, 6.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(10, 24).addCuboid(-2.0f, 4.25f, 6.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(14, 24).addCuboid(-2.0f, 4.25f, 3.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0f, 9.25f, 6.25f, 1.0f, 1.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0f, 7.25f, 6.25f, 1.0f, 1.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0f, 5.25f, 6.25f, 1.0f, 1.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(0, 33).addCuboid(-1.0f, 0.75f, 4.25f, 2.0f, 2.0f, 2.0f, 0.0f, false);
        tank.setTextureOffset(8, 34).addCuboid(-2.75f, 3.75f, 4.75f, 1.0f, 1.0f, 1.0f, 0.0f, false);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25f;

        for (int i = 0; i < 7; i++) {
            ModelPart ink = new ModelPart(this);
            ink.setPivot(0.0f, inkBarY, -0.75f);
            tank.addChild(ink);

            ink.setTextureOffset(52, 0).addCuboid(-1.5f, -12F, 4.5f, 3, 1, 3, 0);

            inkPieces.add(ink);
        }

        torso = new ModelPart(this);
        torso.addChild(top);
    }
}
