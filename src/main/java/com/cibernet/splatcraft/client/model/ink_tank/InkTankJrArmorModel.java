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
        top.setPivot(0.0F, -0.25F, 0.0F);
        top.setTextureOffset(0, 0).addCuboid(-4.8056F, -0.25F, -2.5F, 9.0F, 12.0F, 5.0F, 0.0F, false);
        top.setTextureOffset(31, 0).addCuboid(-1.0F, 3.0F, 2.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        tank = new ModelPart(this);
        tank.setPivot(0.0F, 0.75F, 0.75F);
        top.addChild(tank);
        tank.setTextureOffset(20, 18).addCuboid(-2.0F, 1.5F, 3.75F, 4.0F, 2.0F, 4.0F, 0.0F, false);
        tank.setTextureOffset(8, 33).addCuboid(-3.5F, 3.15F, 4.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        tank.setTextureOffset(12, 39).addCuboid(0.9875F, 5.25F, 7.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(12, 39).addCuboid(0.9875F, 7.25F, 7.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(12, 39).addCuboid(0.9875F, 9.25F, 7.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(14, 24).addCuboid(-3.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(10, 24).addCuboid(-3.0F, 4.25F, 7.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(6, 24).addCuboid(2.0F, 4.25F, 7.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(0, 24).addCuboid(2.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(18, 25).addCuboid(-3.0F, 11.25F, 3.25F, 6.0F, 1.0F, 5.0F, 0.0F, false);
        tank.setTextureOffset(0, 18).addCuboid(-2.5F, 3.25F, 3.25F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        tank.setTextureOffset(31, 2).addCuboid(-0.5F, 1.75F, 2.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);

        tag = new ModelPart(this);
        tag.setPivot(-3.1168F, 2.8445F, 8.9821F);
        setRotationAngle(tag, -0.1309F, -0.3927F, -0.3054F);
        tag.setTextureOffset(8, 63).addCuboid(-0.8541F, 0.6055F, -2.1381F, 2, 0, 2, 0.0F);
        top.addChild(tag);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25F;

        for (int i = 0; i < 7; i++) {
            ModelPart ink = new ModelPart(this);
            ink.setPivot(0.0F, inkBarY, -0.75F);
            tank.addChild(ink);

            ink.setTextureOffset(110, 0).addCuboid(-2.5F, -12.0F, 4.5F, 5, 1, 4, 0.0F);

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
