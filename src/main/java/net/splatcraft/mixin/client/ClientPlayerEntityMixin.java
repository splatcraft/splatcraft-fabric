package net.splatcraft.mixin.client;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.splatcraft.api.component.PlayerDataComponent;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.impl.client.entity.ClientPlayerEntityAccess;
import net.splatcraft.impl.entity.PackedInput;
import net.splatcraft.impl.entity.access.InputPlayerEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.splatcraft.api.client.util.ClientUtil.*;
import static net.splatcraft.api.util.Color.*;
import static net.splatcraft.api.util.SplatcraftConstants.*;
import static net.splatcraft.impl.client.network.NetworkingClient.*;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements InputPlayerEntityAccess, ClientPlayerEntityAccess {
    @Shadow public Input input;

    @Unique private PackedInput packedInput = PackedInput.EMPTY;

    @Unique private float inkOverlayTick = 0;
    @Unique private Integer inkOverlayColor = null;

    private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Unique
    @Override
    public PackedInput getPackedInput() {
        return this.packedInput;
    }

    @Unique
    @Override
    public float getInkOverlayTick() {
        return this.inkOverlayTick;
    }

    @Unique
    @Override
    public int getInkOverlayColor() {
        return this.inkOverlayColor == null ? 0xFFFFFF : this.inkOverlayColor;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z", shift = At.Shift.BEFORE))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity that = (ClientPlayerEntity) (Object) this;
        PlayerDataComponent data = PlayerDataComponent.get(that);

        // input
        PackedInput input = PackedInput.of(this.input);
        this.packedInput = input;
        clientInput(input);

        // ink overlay tick - perform this logic in the player entity as I can't be asked with tickDelta
        float maxHealth = this.getMaxHealth();
        float health = this.getAbilities().invulnerable ? maxHealth : (int) this.getHealth();

        float buffer = (maxHealth * 0.3f);
        float damage = maxHealth - (health + buffer);
        float maxDamage = maxHealth - buffer;

        float desired = damage <= 0 ? 0 : (damage / maxDamage) * MAX_INK_OVERLAYS;
        if (this.isDead()) desired++;
        boolean greater = desired > this.inkOverlayTick;
        float overlayInterp = this.inkOverlayTick + (0.05f * (greater ? 1 : -1));
        this.inkOverlayTick = greater ? Math.min(desired, overlayInterp) : Math.max(desired, overlayInterp);

        // ink overlay color
        if (inkOverlayColor == null || !(this.inkOverlayTick > 0)) {
            this.inkOverlayColor = getDecimalColor(data.getInkColor());
        } else {
            if (this.world.getBlockEntity(this.getSteppingPos()) instanceof Inkable inkable) {
                this.inkOverlayColor = interpolate(0.1f, this.inkOverlayColor, getDecimalColor(inkable.getInkColor()));
            }
        }
    }
}
