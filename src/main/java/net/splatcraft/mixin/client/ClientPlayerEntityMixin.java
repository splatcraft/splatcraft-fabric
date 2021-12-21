package net.splatcraft.mixin.client;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.InkEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow public Input input;

    private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(Z)V"))
    private void onTickMovement(CallbackInfo ci) {
        ClientPlayerEntity that = ClientPlayerEntity.class.cast(this);
        PlayerDataComponent data = PlayerDataComponent.get(that);
        if (data.isSquid() && ((InkEntityAccess) this).canClimbInk()) {
            if (this.getVelocity().y < (this.input.jumping ? 0.46f : 0.4f)) {
                this.updateVelocity(0.07f * (this.input.jumping ? 1.9f : 1.7f), new Vec3d(this.getVelocity().x, this.forwardSpeed * 10, this.getVelocity().z));
            }
            if (this.getVelocity().y <= 0 && !this.input.sneaking) {
                this.updateVelocity(0.035f, new Vec3d(this.getVelocity().x, 1.0f, this.getVelocity().z));
            }
        }
    }
}
