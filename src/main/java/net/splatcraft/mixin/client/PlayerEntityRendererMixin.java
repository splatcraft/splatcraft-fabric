package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.splatcraft.client.render.entity.InkSquidEntityModelRenderer;
import net.splatcraft.entity.access.InkEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.splatcraft.client.util.ClientUtil.*;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Unique private final InkSquidEntityModelRenderer<AbstractClientPlayerEntity> inkSquidRenderer = new InkSquidEntityModelRenderer<>(entityRendererFactoryContext());

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void render(AbstractClientPlayerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, int light, CallbackInfo ci) {
        if (((InkEntityAccess) entity).isInSquidForm()) {
            this.inkSquidRenderer.render(entity, yaw, tickDelta, matrices, vertices, light);
            ci.cancel();
        }
    }
}
