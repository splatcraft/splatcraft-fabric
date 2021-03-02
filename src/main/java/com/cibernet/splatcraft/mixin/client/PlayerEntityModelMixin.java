package com.cibernet.splatcraft.mixin.client;

import com.cibernet.splatcraft.handler.PlayerPoseHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityModel.class)
public class PlayerEntityModelMixin<T extends LivingEntity> {
    @Inject(method = "setAngles", at = @At("TAIL"))
    private void setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerPoseHandler.onPlayerModelSetAngles(PlayerEntityModel.class.cast(this), (PlayerEntity) livingEntity);
        }
    }
}
