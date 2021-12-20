package net.splatcraft.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.splatcraft.entity.InkableCaster;
import net.splatcraft.inkcolor.Inkable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.splatcraft.util.SplatcraftUtil.createSplatfestBandRefreshScreenHandlerListener;
import static net.splatcraft.util.SplatcraftUtil.deathInkableEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "onScreenHandlerOpened", at = @At("TAIL"))
    private void onOnScreenHandlerOpened(ScreenHandler screenHandler, CallbackInfo ci) {
        ServerPlayerEntity that = ServerPlayerEntity.class.cast(this);
        screenHandler.addListener(createSplatfestBandRefreshScreenHandlerListener(that));
    }

    // spawn ink squid soul particle on death
    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onOnDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity that = ServerPlayerEntity.class.cast(this);
        if (!this.world.isClient && that instanceof Inkable) deathInkableEntity(((InkableCaster) that).toInkable());
    }
}
