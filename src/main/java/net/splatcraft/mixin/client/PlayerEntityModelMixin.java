package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.splatcraft.item.weapon.ShooterItem;
import net.splatcraft.item.weapon.WeaponItem;
import net.splatcraft.item.weapon.settings.ShooterSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin<T extends LivingEntity> extends BipedEntityModel<T> {
    @Shadow @Final public ModelPart leftSleeve;
    @Shadow @Final public ModelPart rightSleeve;

    private PlayerEntityModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            ItemStack stack = entity.getActiveItem();
            if (stack.getItem() instanceof WeaponItem weapon) {
                Hand activeHand = entity.getActiveHand();
                Arm mainArm = entity.getMainArm();

                ModelPart partm = (activeHand == Hand.MAIN_HAND && mainArm == Arm.LEFT) || (activeHand == Hand.OFF_HAND && mainArm == Arm.RIGHT)
                    ? this.leftArm : this.rightArm;
                ModelPart parto = partm.equals(this.leftArm)
                    ? this.rightArm : this.leftArm;

                switch (weapon.getWeaponPose()) {
                    case SHOOTING -> {
                        if (weapon instanceof ShooterItem shooter) {
                            ShooterSettings settings = shooter.getWeaponSettings();
                            float raise = Math.min(entity.getItemUseTime() / (float) (settings.getInitialDelay() + settings.getFireInterval()), 1.0f);
                            partm.roll = this.head.roll - 0.1f;
                            partm.pitch = this.head.pitch - (raise * ((float) Math.PI / 2F));
                        }
                    }
                    case DUAL_SHOOTING -> {
                        ItemStack stacko = entity.getOffHandStack();
                        if (stacko.getItem() instanceof WeaponItem w && w.getWeaponPose() == WeaponItem.Pose.DUAL_SHOOTING) {
                            parto.pivotY = -0.1f + this.head.pivotY;
                            parto.pivotX = -((float) Math.PI / 2F) + this.head.pivotX;
                        }
                    }
                    case ROLLING -> {
                        partm.pitch = -0.31f;
                        partm.roll = 0.0f;
                    }
                    /*case BUCKET_SWING -> {
                        float animTime = ((SlosherItem) mainStack.getItem()).startupTicks * 0.5f;
                        mainHand.pivotY = 0;
                        mainHand.pivotX = -0.36f;
                        float angle = entity.getItemUseTime() / animTime;
                        if (angle < 6.5f) mainHand.pivotX = MathHelper.cos(angle * 0.6662f);
                    }*/
                }

                this.leftSleeve.copyTransform(this.leftArm);
                this.rightSleeve.copyTransform(this.rightArm);
            }
        }
    }
}
