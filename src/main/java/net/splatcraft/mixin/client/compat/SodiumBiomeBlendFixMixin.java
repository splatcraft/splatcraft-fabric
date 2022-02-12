package net.splatcraft.mixin.client.compat;

import me.jellysquid.mods.sodium.client.model.quad.blender.ColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.LinearColorBlender;
import me.jellysquid.mods.sodium.client.render.pipeline.ChunkRenderCache;
import net.splatcraft.client.config.ClientCompatConfig;
import net.splatcraft.client.model.quad.blender.InkColorBlender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRenderCache.class)
public class SodiumBiomeBlendFixMixin {
    /**
     * Disables smooth biome blender if configured.
     */
    @Inject(method = "createBiomeColorBlender", at = @At("RETURN"), cancellable = true, remap = false)
    private void disableSmoothBlending(CallbackInfoReturnable<ColorBlender> cir) {
        if (ClientCompatConfig.INSTANCE.sodium_inkBiomeBlendFix.getValue()) {
            ColorBlender blender = cir.getReturnValue();
            if (blender instanceof LinearColorBlender) cir.setReturnValue(new InkColorBlender());
        }
    }
}
