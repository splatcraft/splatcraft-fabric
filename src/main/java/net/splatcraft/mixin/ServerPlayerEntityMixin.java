package net.splatcraft.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.splatcraft.util.SplatcraftUtil.createSplatfestBandRefreshScreenHandlerListener;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onScreenHandlerOpened", at = @At("TAIL"))
    private void onOnScreenHandlerOpened(ScreenHandler screenHandler, CallbackInfo ci) {
        ServerPlayerEntity that = ServerPlayerEntity.class.cast(this);
        screenHandler.addListener(createSplatfestBandRefreshScreenHandlerListener(that));
    }
}
