package net.splatcraft.test.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SplashOverlay.class)
public class SplashOverlayMixin {
    @Shadow @Final private boolean reloading;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V"))
    private void onGameInitialize(CallbackInfo ci) {
        if (this.reloading) return;

        // log registries
        Registry.REGISTRIES.forEach(this::logSize);
        BuiltinRegistries.REGISTRIES.forEach(this::logSize);
    }

    private void logSize(Registry<?> registry) {
        Identifier registryId = registry.getKey().getValue();
        Splatcraft.LOGGER.info("registry {}: {}", registryId.getPath(), registry.size());
    }
}
