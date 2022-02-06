package net.splatcraft.mixin.client.compat;

import me.jellysquid.mods.sodium.client.model.quad.blender.ColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.FlatColorBlender;
import me.jellysquid.mods.sodium.client.render.pipeline.ChunkRenderCache;
import net.splatcraft.client.config.ClientCompatConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRenderCache.class)
public class SodiumBiomeBlendFixMixin {
    /**
     * Disables smooth biome blender if configured.
     */
    @Inject(method = "createBiomeColorBlender", at = @At("HEAD"), cancellable = true, remap = false)
    private void disableSmoothBlending(CallbackInfoReturnable<ColorBlender> cir) {
        if (ClientCompatConfig.INSTANCE.sodium_inkBiomeBlendFix.getValue()) cir.setReturnValue(new FlatColorBlender());
    }
}
