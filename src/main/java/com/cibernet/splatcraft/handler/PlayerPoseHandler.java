package com.cibernet.splatcraft.handler;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import com.cibernet.splatcraft.item.AbstractWeaponItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class PlayerPoseHandler {
    public static void onPlayerModelSetAngles(PlayerEntityModel<?> $this, PlayerEntity player) {
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
        if ($this != null && player != null && !data.isSquid()) {
            Hand activeHand = player.getActiveHand();
            Arm handSide = player.getMainArm();

            if (activeHand != null) {
                ModelPart mainHand = activeHand == Hand.MAIN_HAND && handSide == Arm.LEFT || activeHand == Hand.OFF_HAND && handSide == Arm.RIGHT ? $this.leftArm : $this.rightArm;
                ModelPart offHand = mainHand.equals($this.leftArm) ? $this.rightArm : $this.leftArm;

                ItemStack mainStack = player.getStackInHand(activeHand);
                ItemStack offStack = player.getStackInHand(Hand.values()[(activeHand.ordinal() + 1) % Hand.values().length]);
                int useTime = player.getItemUseTimeLeft();

                if (mainStack.getItem() instanceof AbstractWeaponItem && useTime > 0 || data.getCooldown() != null && data.getCooldown().getTime() > 0) {
                    // useTime = mainStack.getItem().getMaxUseTime(mainStack) - useTime;
                    switch (((AbstractWeaponItem) mainStack.getItem()).getPose()) {
                        case DUAL_FIRE:
                            if (offStack.getItem() instanceof AbstractWeaponItem && ((AbstractWeaponItem) offStack.getItem()).getPose().equals(WeaponPose.DUAL_FIRE)) {
                                offHand.pivotY = -0.1F + $this.getHead().pivotY;
                                offHand.pivotX = -((float) Math.PI / 2F) + $this.getHead().pivotX;
                            }
                        case FIRE:
                            mainHand.pivotY = -0.1F + $this.getHead().pivotY;
                            mainHand.pivotX = -((float) Math.PI / 2F) + $this.getHead().pivotX;
                            break;
                        case ROLL:
                            mainHand.pitch = -0.30978097624F;
                            mainHand.roll = 0.0F;
                            break;
                    /*case BUCKET_SWING:
                        float animTime = ((SlosherItem) mainStack.getItem()).startupTicks * 0.5f; TODO
                        mainHand.pivotY = 0;
                        mainHand.pivotX = -0.36f;

                        float angle = useTime / animTime;

                        if (angle < 6.5f)
                            mainHand.pivotX = MathHelper.cos(angle * 0.6662F);
                        break;*/
                    }

                    $this.leftSleeve.copyPositionAndRotation($this.leftArm);
                    $this.rightSleeve.copyPositionAndRotation($this.rightArm);
                }
            }
        }
    }

    public enum WeaponPose {
        NONE,
        FIRE,
        DUAL_FIRE,
        ROLL,
        BOW_CHARGE,
        BUCKET_SWING
    }
}
