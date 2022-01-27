package net.splatcraft.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.splatcraft.Splatcraft;
import net.splatcraft.client.config.ClientConfig;
import net.splatcraft.client.config.enums.HealthInkOverlay;
import net.splatcraft.client.entity.ClientPlayerEntityAccess;
import net.splatcraft.util.Color;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

import static net.splatcraft.client.util.ClientUtil.*;
import static net.splatcraft.util.SplatcraftConstants.*;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
    @Shadow @Final private MinecraftClient client;
    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;

    private static final Function<Integer, Identifier> INDEX_TO_INK_TEXTURE = Util.memoize(i -> texture("misc/ink_overlay_" + i));

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getLastFrameDuration()F", shift = At.Shift.BEFORE, ordinal = 0))
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (this.client.player == null) return;

        if (!ClientConfig.INSTANCE.healthInkOverlay.is(HealthInkOverlay.OFF)) {
            this.client.getProfiler().push("%s:%s".formatted(Splatcraft.MOD_ID, "inkOverlay"));

            float inkOverlayTick = ((ClientPlayerEntityAccess) this.client.player).getInkOverlayTick();
            if (inkOverlayTick > 0) {
                for (int i = 0; i < MAX_INK_OVERLAYS; i++) {
                    if (inkOverlayTick > i) { // check if the tick is at least at this layer of overlays
                        if (inkOverlayTick > i + 1) { // if the tick is over the layer
                            this.renderInkOverlay(i);
                        } else {
                            float mod = inkOverlayTick % 1;
                            if (mod == 0) this.renderInkOverlay(i);
                                else this.renderInkOverlay(i, mod);
                        }
                    }
                }

                if (this.client.player.isDead()) this.renderInkOverlay(MAX_INK_OVERLAYS, inkOverlayTick - MAX_INK_OVERLAYS);
            }

            this.client.getProfiler().pop();
        }
    }

    private void renderInkOverlay(int index, float opacity) {
        if (this.client.player == null) return;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Color color = Color.of(((ClientPlayerEntityAccess) this.client.player).getInkOverlayColor());
        RenderSystem.setShaderColor(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, opacity * 0.9f);

        RenderSystem.setShaderTexture(0, INDEX_TO_INK_TEXTURE.apply(index));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0, this.scaledHeight, -90.0).texture(0.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, 0.0, -90.0).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0f, 0.0f).next();
        tessellator.draw();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderInkOverlay(int index) {
        renderInkOverlay(index, 1.0f);
    }
}
