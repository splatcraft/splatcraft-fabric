package net.splatcraft.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.client.config.enums.PreventBobView;
import net.splatcraft.component.PlayerDataComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    // disable bob view if configured
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void onBobView(MatrixStack matrices, float f, CallbackInfo ci) {
        PlayerDataComponent data = PlayerDataComponent.get(this.client.player);
        if (data.isSquid()) {
            PreventBobView preventBobView = ClientConfig.INSTANCE.preventBobViewWhenSquid.getValue();
            if (preventBobView == PreventBobView.ALWAYS
            || (preventBobView == PreventBobView.SUBMERGED && data.isSubmerged())) ci.cancel();
        }
    }
}
