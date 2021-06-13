package com.cibernet.splatcraft.mixin.client;

import com.cibernet.splatcraft.client.renderer.entity.player.AnimatablePlayerEntityRenderer;
import com.cibernet.splatcraft.client.renderer.entity.player.PlayerEntityInkSquidRenderer;
import com.cibernet.splatcraft.client.signal.SignalRendererManager;
import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.entity.player.signal.AnimatablePlayerEntity;
import com.cibernet.splatcraft.handler.PlayerHandler;
import com.cibernet.splatcraft.util.Util;
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

import java.util.Map;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    private PlayerEntityInkSquidRenderer splatcraft_playerInkSquidRenderer;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(AbstractClientPlayerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, int light, CallbackInfo ci) {
        this.splatcraft_validateModelCaches(entity);

        if (LazyPlayerDataComponent.isSquid(entity)) {
            if (!PlayerHandler.shouldBeSubmerged(entity)) {
                splatcraft_playerInkSquidRenderer.render(entity, yaw, tickDelta, matrices, vertices, light);
            }
            ci.cancel();
        } else if (SignalRendererManager.PLAYER_SIGNAL_ENTITY_RENDERERS.containsKey(entity)) {
            AnimatablePlayerEntityRenderer renderer = SignalRendererManager.PLAYER_SIGNAL_ENTITY_RENDERERS.get(entity);
            AnimatablePlayerEntity animation = renderer.getAnimatable();
            if (animation.isRunning() && AnimatablePlayerEntity.shouldContinue(animation.player)) {
                renderer.render(entity, yaw, tickDelta, matrices, vertices, light);
                ci.cancel();
            } else {
                SignalRendererManager.reset(entity);
            }
        }
    }

    private void splatcraft_validateModelCaches(AbstractClientPlayerEntity entity) {
        if (splatcraft_playerInkSquidRenderer == null) {
            splatcraft_playerInkSquidRenderer = new PlayerEntityInkSquidRenderer(Util.createEntityRendererFactoryContext());
        }

        Map<AbstractClientPlayerEntity, AnimatablePlayerEntityRenderer> renderers = SignalRendererManager.PLAYER_SIGNAL_ENTITY_RENDERERS;
        if (!renderers.containsKey(entity) && SignalRendererManager.PLAYER_TO_SIGNAL_MAP.containsKey(entity)) {
            PlayerEntityRenderer $this = PlayerEntityRenderer.class.cast(this);
            renderers.put(entity, SignalRendererManager.createRenderer(Util.createEntityRendererFactoryContext(), entity, $this.getModel()));
        }
        renderers.entrySet().removeAll(
            renderers.entrySet()
                .stream().filter(entry -> entry.getValue().isDead())
                .collect(Collectors.toSet())
        );
    }
}
