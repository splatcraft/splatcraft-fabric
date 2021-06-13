package com.cibernet.splatcraft.mixin.client;

import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.handler.PlayerHandler;
import com.cibernet.splatcraft.item.weapon.AbstractWeaponItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Shadow public Input input;

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(Z)V", shift = At.Shift.AFTER))
    private void tickMovement(CallbackInfo ci) {
        ClientPlayerEntity $this = ClientPlayerEntity.class.cast(this);

        if (LazyPlayerDataComponent.isSquid($this) && !$this.getAbilities().flying) {
            if (PlayerHandler.canClimb($this)) {
                if ($this.getVelocity().getY() < (input.jumping ? 0.46f : 0.4f)) {
                    $this.updateVelocity(0.07f * (input.jumping ? 1.9f : 1.7f), new Vec3d(0.0f, $this.forwardSpeed * 10, 0.0f));
                }
                if ($this.getVelocity().getY() <= 0 && !input.sneaking) {
                    $this.updateVelocity(0.035f, new Vec3d(0.0f, 1f, 0.0f));
                }
            }

            this.input.sneaking = false;
        }

        if ($this.isUsingItem()) {
            ItemStack stack = $this.getActiveItem();
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (item instanceof AbstractWeaponItem) {
                    float mobility =  ((AbstractWeaponItem) item).getMobility($this);

                    $this.setSprinting(mobility >= 1.0f && (this.input.movementForward > 0 || this.input.movementSideways > 0));

                    this.input.movementSideways = (this.input.movementSideways / 0.2f) * mobility;
                    this.input.movementForward = (this.input.movementForward / 0.2f) * mobility;
                }
            }
        }
    }
}
