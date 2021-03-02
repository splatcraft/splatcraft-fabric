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
        top.setPivot(0.0F, -0.25F, 0.0F);
        top.setTextureOffset(0, 0).addCuboid(-4.75F, -0.25F, -2.5F, 9.0F, 12.0F, 5.0F, 0.0F, false);
        top.setTextureOffset(30, 0).addCuboid(-1.0F, 3.0F, 2.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        tank = new ModelPart(this);
        tank.setPivot(0.0F, 0.75F, 0.25F);
        top.addChild(tank);
        tank.setTextureOffset(0, 19).addCuboid(-2.0F, 3.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        tank.setTextureOffset(20, 28).addCuboid(-1.5F, 2.25F, 3.75F, 3.0F, 1.0F, 3.0F, 0.0F, false);
        tank.setTextureOffset(22, 32).addCuboid(-2.0F, 2.0F, 4.75F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(21, 35).addCuboid(-0.5F, 2.0F, 3.25F, 1.0F, 2.0F, 4.0F, 0.0F, false);
        tank.setTextureOffset(16, 19).addCuboid(-2.0F, 11.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        tank.setTextureOffset(20, 24).addCuboid(-1.5F, 11.75F, 3.75F, 3.0F, 1.0F, 3.0F, 0.0F, false);
        tank.setTextureOffset(0, 24).addCuboid(1.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(6, 24).addCuboid(1.0F, 4.25F, 6.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(10, 24).addCuboid(-2.0F, 4.25F, 6.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(14, 24).addCuboid(-2.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0F, 9.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0F, 7.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0F, 5.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(0, 33).addCuboid(-1.0F, 0.75F, 4.25F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        tank.setTextureOffset(8, 34).addCuboid(-2.75F, 3.75F, 4.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25F;

        for (int i = 0; i < 7; i++) {
            ModelPart ink = new ModelPart(this);
            ink.setPivot(0.0F, inkBarY, -0.75F);
            tank.addChild(ink);

            ink.setTextureOffset(52, 0).addCuboid(-1.5F, -12F, 4.5F, 3, 1, 3, 0);

            inkPieces.add(ink);
        }

        torso = new ModelPart(this);
        torso.addChild(top);
    }
}
