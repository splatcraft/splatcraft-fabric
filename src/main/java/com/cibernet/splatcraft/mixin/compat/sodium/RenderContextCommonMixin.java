package com.cibernet.splatcraft.mixin.compat.sodium;

import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import me.jellysquid.mods.sodium.client.model.quad.blender.BiomeColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.FlatBiomeColorBlender;
import me.jellysquid.mods.sodium.client.render.pipeline.RenderContextCommon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderContextCommon.class)
public class RenderContextCommonMixin {
    @Inject(method = "createBiomeColorBlender", at = @At("HEAD"), cancellable = true, remap = false)
    private static void disableSmoothBlending(CallbackInfoReturnable<BiomeColorBlender> cir) {
        if (SplatcraftConfig.COMPATIBILITY.sodium_inkBiomeBlendFix.value) {
            cir.setReturnValue(new FlatBiomeColorBlender());
        }
    }
}
