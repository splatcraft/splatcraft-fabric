package com.cibernet.splatcraft.client.model.ink_tank;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class AbstractInkTankArmorModel extends BipedEntityModel<LivingEntity> {
    protected List<ModelPart> inkPieces = new ArrayList<>();
    protected static final float inkBarY = 0;

    /*TODO @SuppressWarnings("all")
    public static final ImmutableMap<Item, AbstractInkTankArmorModel> ITEM_TO_MODEL_MAP = ImmutableMap.of(
        SplatcraftItems.INK_TANK, new InkTankArmorModel(),
        SplatcraftItems.CLASSIC_INK_TANK, new ClassicInkTankArmorModel(),
        SplatcraftItems.INK_TANK_JR, new InkTankJrArmorModel(),
        SplatcraftItems.ARMORED_INK_TANK, new ArmoredInkTankArmorModel()
    );*/

    public AbstractInkTankArmorModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setAngles(LivingEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    public void setInkLevels(float inkPctg) {
        for (int i = 1; i <= inkPieces.size(); i++) {
            ModelPart box = inkPieces.get(i - 1);
            if (inkPctg == 0) {
                box.visible = false;
            } else {
                box.visible = true;
                box.pivotY = 23.25f - Math.min(i * inkPctg, i);
            }
        }
    }

    /*TODO public static AbstractInkTankArmorModel getModelFromItem(Item item) {
        return ITEM_TO_MODEL_MAP.get(item);
    }*/
}
