package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.splatcraft.client.entity.ClientPlayerEntityAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

import static net.splatcraft.client.util.ClientUtil.texture;
import static net.splatcraft.util.SplatcraftConstants.MAX_INK_OVERLAYS;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
    @Shadow protected abstract void renderOverlay(Identifier texture, float opacity);
    @Shadow @Final private MinecraftClient client;

    private static final Function<Integer, Identifier> INDEX_TO_INK_TEXTURE = Util.memoize(i -> texture("misc/ink_overlay_" + i));

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getLastFrameDuration()F", shift = At.Shift.BEFORE, ordinal = 0))
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (this.client.player == null) return;

        float inkOverlayTick = ((ClientPlayerEntityAccess) this.client.player).getInkOverlayTick();
        if (inkOverlayTick >= 1) { // initial buffer (prevents weirdness when sprint jumping over enemy ink, for example)
            inkOverlayTick--; // remove buffer
            for (int i = 0; i < MAX_INK_OVERLAYS; i++) {
                if (inkOverlayTick > i) { // check if the tick is at least at this layer of overlays
                    Identifier texture = INDEX_TO_INK_TEXTURE.apply(i);
                    if (inkOverlayTick > i + 1) { // if the tick is over the layer
                        this.renderOverlay(texture, 1.0f);
                    } else {
                        float mod = inkOverlayTick % 1;
                        if (mod == 0) this.renderOverlay(texture, 1.0f);
                            else this.renderOverlay(texture, mod);
                    }
                }
            }
        }
    }
}
