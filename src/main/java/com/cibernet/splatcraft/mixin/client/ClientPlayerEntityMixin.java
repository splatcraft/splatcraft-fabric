package com.cibernet.splatcraft.mixin.client;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.item.AbstractWeaponItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Shadow
    public Input input;

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(Z)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void tickMovement(CallbackInfo ci) {
        ClientPlayerEntity player = ClientPlayerEntity.class.cast(this);
        Input input = this.input;

        if (PlayerDataComponent.isSquid(player) && !player.abilities.flying) {
            if (InkBlockUtils.canClimb(player)) {
                if (player.getVelocity().getY() < (input.jumping ? 0.46f : 0.4f)) {
                    player.updateVelocity(0.07f * (input.jumping ? 1.9f : 1.7f), new Vec3d(0.0f, player.forwardSpeed * 10, 0.0f));
                }
                if (player.getVelocity().getY() <= 0 && !input.sneaking) {
                    player.updateVelocity(0.035f, new Vec3d(0.0f, 1f, 0.0f));
                }
            }

            input.sneaking = false;
        }

        if (player.isUsingItem()) {
            ItemStack stack = player.getActiveItem();
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof AbstractWeaponItem) {
                    input.movementSideways *= 5.0F;
                    input.movementForward *= 5.0F;
                }
            }
        }
    }
}
