package com.cibernet.splatcraft.client.signal;

import com.cibernet.splatcraft.client.renderer.entity.player.AnimatablePlayerEntityRenderer;
import com.cibernet.splatcraft.network.SplatcraftNetworkingConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class SignalRendererManager {
    public static Map<AbstractClientPlayerEntity, Signal> PLAYER_TO_SIGNAL_MAP = new HashMap<>();
    public static final Map<AbstractClientPlayerEntity, AnimatablePlayerEntityRenderer> PLAYER_SIGNAL_ENTITY_RENDERERS = new HashMap<>();

    public static void sendSignal(AbstractClientPlayerEntity player, Signal signal) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());
        buf.writeIdentifier(signal.id);

        ClientPlayNetworking.send(SplatcraftNetworkingConstants.PLAY_PLAYER_SIGNAL_PACKET_ID, buf);
    }
    public static void playSignal(AbstractClientPlayerEntity player, Signal signal) {
        PLAYER_TO_SIGNAL_MAP.put(player, signal);
        PLAYER_SIGNAL_ENTITY_RENDERERS.remove(player);
    }

    public static AnimatablePlayerEntityRenderer createRenderer(EntityRenderDispatcher dispatcher, AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model) {
        return new AnimatablePlayerEntityRenderer(dispatcher, player, SignalRendererManager.PLAYER_TO_SIGNAL_MAP.get(player), model.thinArms);
    }

    public static ActionResult reset(PlayerEntity player) {
        if (player instanceof AbstractClientPlayerEntity) {
            PLAYER_TO_SIGNAL_MAP.remove(player);
            PLAYER_SIGNAL_ENTITY_RENDERERS.remove(player);
        }

        return ActionResult.PASS;
    }
    public static <T> TypedActionResult<T> reset(PlayerEntity player, T data) {
        if (player instanceof AbstractClientPlayerEntity) {
            SignalRendererManager.reset(player);
        }

        return TypedActionResult.pass(data);
    }
}
