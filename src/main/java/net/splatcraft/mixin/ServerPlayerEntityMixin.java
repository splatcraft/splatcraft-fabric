package net.splatcraft.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.PackedInput;
import net.splatcraft.entity.access.InkEntityAccess;
import net.splatcraft.entity.access.InputPlayerEntityAccess;
import net.splatcraft.entity.access.PlayerEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements InputPlayerEntityAccess, InkEntityAccess {
    private PackedInput packedInput = PackedInput.EMPTY;

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Override
    public PackedInput getPackedInput() {
        return this.packedInput;
    }

    @Override
    public void setPackedInput(PackedInput input) {
        this.packedInput = input;
    }

    @Inject(method = "onScreenHandlerOpened", at = @At("TAIL"))
    private void onOnScreenHandlerOpened(ScreenHandler screenHandler, CallbackInfo ci) {
        ServerPlayerEntity that = ServerPlayerEntity.class.cast(this);
        screenHandler.addListener(new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                ((PlayerEntityAccess) that).checkSplatfestBand();
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {}
        });
    }

    /**
     * Disables squid form on death.
     */
    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onOnDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity that = ServerPlayerEntity.class.cast(this);
        if (this.isInSquidForm()) {
            PlayerDataComponent data = PlayerDataComponent.get(that);
            data.setSquid(false);

            this.setInvisible(true);
        }
    }
}
