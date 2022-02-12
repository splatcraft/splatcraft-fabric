package net.splatcraft.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.moddingplayground.frame.api.config.option.EnumOption;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.client.config.enums.HealthInkOverlay;
import net.splatcraft.client.config.enums.PreventBobView;
import net.splatcraft.entity.access.InkEntityAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    /**
     * Disables view bobbing if configured.
     */
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void onBobView(MatrixStack matrices, float f, CallbackInfo ci) {
        if (this.client.player != null) {
            InkEntityAccess access = (InkEntityAccess) this.client.player;
            if (access.isInSquidForm()) {
                EnumOption<PreventBobView> opt = ClientConfig.INSTANCE.preventBobViewWhenSquid;
                if (opt.is(PreventBobView.ALWAYS) || (opt.is(PreventBobView.SUBMERGED) && access.isSubmergedInInk())) ci.cancel();
            }
        }
    }

    /**
     * Disables hurt bob when ink health overlay is enabled.
     */
    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void onBobViewWhenHurt(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (ClientConfig.INSTANCE.healthInkOverlay.is(HealthInkOverlay.ON)) ci.cancel();
    }
}
