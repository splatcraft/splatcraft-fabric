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
        top.setPivot(0.0F, 0.0F, 0.0F);
        top.setTextureOffset(0, 112).addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        middle = new ModelPart(this);
        middle.setPivot(0.0F, -0.25F, 0.0F);
        middle.setTextureOffset(0, 0).addCuboid(-4.75F, -0.25F, -2.5F, 9.0F, 12.0F, 5.0F, 0.0F, false);
        middle.setTextureOffset(31, 0).addCuboid(-1.0F, 3.0F, 2.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        tank = new ModelPart(this);
        tank.setPivot(0.0F, 0.75F, 0.75F);
        middle.addChild(tank);
        tank.setTextureOffset(31, 2).addCuboid(-0.5F, 1.75F, 2.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        tank.setTextureOffset(0, 19).addCuboid(-2.0F, 3.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        tank.setTextureOffset(16, 19).addCuboid(-2.0F, 11.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        tank.setTextureOffset(0, 24).addCuboid(1.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(6, 24).addCuboid(1.0F, 4.25F, 6.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(10, 24).addCuboid(-2.0F, 4.25F, 6.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(14, 24).addCuboid(-2.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0F, 9.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0F, 7.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(12, 39).addCuboid(0.0F, 5.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        tank.setTextureOffset(0, 33).addCuboid(-1.0F, 2.25F, 4.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        tank.setTextureOffset(8, 33).addCuboid(-3.5F, 2.5F, 4.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25F;

        for (int i = 0; i < 7; i++) {
            ModelPart ink = new ModelPart(this);
            ink.setPivot(0.0F, inkBarY, -0.75F);
            tank.addChild(ink);

            ink.setTextureOffset(116, 30).addCuboid(-1.5F, -12F, 4.5F, 3, 1, 3, 0);

            inkPieces.add(ink);
        }

        torso = new ModelPart(this);
        torso.addChild(middle);
    }
}
