package com.cibernet.splatcraft.client.model.ink_tank;

import com.cibernet.splatcraft.init.SplatcraftItems;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class AbstractInkTankArmorModel extends BipedEntityModel<LivingEntity> {
    protected List<ModelPart> inkPieces = new ArrayList<>();
    protected float inkBarY = 0;

    @SuppressWarnings("all")
    public static final ImmutableMap<Item, AbstractInkTankArmorModel> ITEM_TO_MODEL_MAP = ImmutableMap.of(
        SplatcraftItems.INK_TANK, new InkTankArmorModel(),
        SplatcraftItems.CLASSIC_INK_TANK, new ClassicInkTankArmorModel(),
        SplatcraftItems.INK_TANK_JR, new InkTankJrArmorModel(),
        SplatcraftItems.ARMORED_INK_TANK, new ArmoredInkTankArmorModel()
    );

    public AbstractInkTankArmorModel() {
        super(1.0f);
    }

    @Override
    public void animateModel(LivingEntity entity, float limbSwing, float limbDistance, float tickDelta) {
        super.animateModel(entity, limbSwing, limbDistance, tickDelta);
    }

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

    @Override
    public void setAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public static AbstractInkTankArmorModel getModelFromItem(Item item) {
        return ITEM_TO_MODEL_MAP.get(item);
    }
}
