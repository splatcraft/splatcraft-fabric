package com.cibernet.splatcraft.mixin.client;

import com.cibernet.splatcraft.client.renderer.entity.ink_squid.PlayerEntityInkSquidRenderer;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.init.SplatcraftComponents;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    private static PlayerEntityInkSquidRenderer renderer;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumers, int light, CallbackInfo ci) {
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
        if (data.isSquid()) {
            if (renderer == null) {
                renderer = new PlayerEntityInkSquidRenderer(PlayerEntityRenderer.class.cast(this).getRenderManager());
            }
            if (!InkBlockUtils.canSwim(player)) {
                renderer.render(player, yaw, tickDelta, matrices, consumers, light);
            }

            ci.cancel();
        }
    }
}
