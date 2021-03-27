package com.cibernet.splatcraft.client.model.ink_tank;

import net.minecraft.client.model.ModelPart;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class InkTankJrArmorModel extends AbstractInkTankArmorModel {
    private final ModelPart top;
    private final ModelPart tank;
    private final ModelPart tag;

    public InkTankJrArmorModel() {
        textureWidth = 128;
        textureHeight = 128;

        top = new ModelPart(this);
        top.setPivot(0.0f, -0.25f, 0.0f);
        top.setTextureOffset(0, 0).addCuboid(-4.8056f, -0.25f, -2.5f, 9.0f, 12.0f, 5.0f, 0.0f, false);
        top.setTextureOffset(31, 0).addCuboid(-1.0f, 3.0f, 2.5f, 2.0f, 1.0f, 1.0f, 0.0f, false);

        tank = new ModelPart(this);
        tank.setPivot(0.0f, 0.75f, 0.75f);
        top.addChild(tank);
        tank.setTextureOffset(20, 18).addCuboid(-2.0f, 1.5f, 3.75f, 4.0f, 2.0f, 4.0f, 0.0f, false);
        tank.setTextureOffset(8, 33).addCuboid(-3.5f, 3.15f, 4.25f, 2.0f, 1.0f, 2.0f, 0.0f, false);
        tank.setTextureOffset(12, 39).addCuboid(0.9875f, 5.25f, 7.25f, 1.0f, 1.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(12, 39).addCuboid(0.9875f, 7.25f, 7.25f, 1.0f, 1.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(12, 39).addCuboid(0.9875f, 9.25f, 7.25f, 1.0f, 1.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(14, 24).addCuboid(-3.0f, 4.25f, 3.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(10, 24).addCuboid(-3.0f, 4.25f, 7.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(6, 24).addCuboid(2.0f, 4.25f, 7.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(0, 24).addCuboid(2.0f, 4.25f, 3.25f, 1.0f, 7.0f, 1.0f, 0.0f, false);
        tank.setTextureOffset(18, 25).addCuboid(-3.0f, 11.25f, 3.25f, 6.0f, 1.0f, 5.0f, 0.0f, false);
        tank.setTextureOffset(0, 18).addCuboid(-2.5f, 3.25f, 3.25f, 5.0f, 1.0f, 5.0f, 0.0f, false);
        tank.setTextureOffset(31, 2).addCuboid(-0.5f, 1.75f, 2.0f, 1.0f, 2.0f, 2.0f, 0.0f, false);

        tag = new ModelPart(this);
        tag.setPivot(-3.1168f, 2.8445f, 8.9821f);
        setRotationAngle(tag, -0.1309f, -0.3927f, -0.3054f);
        tag.setTextureOffset(8, 63).addCuboid(-0.8541f, 0.6055f, -2.1381f, 2, 0, 2, 0.0f);
        top.addChild(tag);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25f;

        for (int i = 0; i < 7; i++) {
            ModelPart ink = new ModelPart(this);
            ink.setPivot(0.0f, inkBarY, -0.75f);
            tank.addChild(ink);

            ink.setTextureOffset(110, 0).addCuboid(-2.5f, -12.0f, 4.5f, 5, 1, 4, 0.0f);

            inkPieces.add(ink);
        }

        torso = new ModelPart(this);
        torso.addChild(top);

    }
    public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.pivotX = x;
        modelRenderer.pivotY = y;
        modelRenderer.pivotZ = z;
    }
}
