package net.splatcraft.mixin.client;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.splatcraft.client.entity.ClientPlayerEntityAccess;
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.entity.InputPlayerEntityAccess;
import net.splatcraft.entity.PackedInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.splatcraft.client.network.NetworkingClient.clientInput;
import static net.splatcraft.entity.damage.SplatcraftDamageSource.ID_INKED;
import static net.splatcraft.util.SplatcraftConstants.MAX_INK_OVERLAYS;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements InputPlayerEntityAccess, ClientPlayerEntityAccess {
    @Shadow public Input input;

    private PackedInput storedForwardSpeed = PackedInput.EMPTY;
    private float inkOverlayTick = 0;

    private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Override
    public PackedInput getPackedInput() {
        return this.storedForwardSpeed;
    }

    @Override
    public float getInkOverlayTick() {
        return this.inkOverlayTick;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z", shift = At.Shift.BEFORE))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity that = ClientPlayerEntity.class.cast(this);

        // input
        PackedInput input = PackedInput.of(this.input);
        this.storedForwardSpeed = input;
        clientInput(input);

        // ink overlay tick
        int inkOverlayIndex = (int) inkOverlayTick + 1;
        this.inkOverlayTick = MathHelper.clamp(
            this.inkOverlayTick + (
                ((InkEntityAccess) that).isOnEnemyInk()
                    ? 0.03f * (1f / inkOverlayIndex + 1) // slower build up
                    : -0.28f * (inkOverlayIndex / 5f)    // faster decay
            ), 0, MAX_INK_OVERLAYS + 1
        );
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getName().equals(ID_INKED)) this.inkOverlayTick = Math.max(1.0f, this.inkOverlayTick) + 1.5f;
    }
}
