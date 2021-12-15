package net.splatcraft.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.splatcraft.client.render.entity.inkable.PlayerInkSquidEntityModelRenderer;
import net.splatcraft.component.PlayerDataComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.splatcraft.util.SplatcraftUtil.entityRendererFactoryContext;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    private final PlayerInkSquidEntityModelRenderer inkSquidRenderer = new PlayerInkSquidEntityModelRenderer(entityRendererFactoryContext());

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void render(AbstractClientPlayerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, int light, CallbackInfo ci) {
        PlayerDataComponent data = PlayerDataComponent.get(entity);
        if (data.isSquid()) {
            if (!data.isSubmerged()) this.inkSquidRenderer.render(entity, yaw, tickDelta, matrices, vertices, light);
            ci.cancel();
        }
    }
}
