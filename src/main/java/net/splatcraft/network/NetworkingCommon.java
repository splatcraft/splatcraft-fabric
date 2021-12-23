package net.splatcraft.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.InputPlayerEntityAccess;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.Inkable;

import static net.splatcraft.network.PacketIdentifiers.*;

public class NetworkingCommon {
    static {
        ServerPlayNetworking.registerGlobalReceiver(KEY_CHANGE_SQUID_FORM, (server, player, handler, buf, responseSender) -> {
            boolean squid = buf.readBoolean();

            PlayerDataComponent data = PlayerDataComponent.get(player);
            if (data.setSquid(squid)) {
                PacketByteBuf returnBuf = PacketByteBufs.create();
                returnBuf.writeBoolean(squid);
                ServerPlayNetworking.send(player, KEY_CHANGE_SQUID_FORM_RESPONSE, returnBuf);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(CLIENT_INPUT, (server, player, handler, buf, responseSender) -> {
            double sidewaysSpeed = buf.readDouble();
            double upwardSpeed = buf.readDouble();
            double forwardSpeed = buf.readDouble();
            ((InputPlayerEntityAccess) player).setInputSpeeds(new Vec3d(sidewaysSpeed, upwardSpeed, forwardSpeed));
        });
    }

    public static <T extends Entity & Inkable> void inkSplashParticleAtPos(T inkable, Vec3d pos, float scale) {
        InkColor inkColor = inkable.getInkColor();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(inkColor.getId());
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeFloat(scale);

        if (inkable.getWorld() instanceof ServerWorld world) {
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, new BlockPos(pos))) {
                ServerPlayNetworking.send(player, INK_SPLASH_PARTICLE_AT_POS, buf);
            }
        }
    }

    public static <T extends Entity & Inkable> void inkSquidSoulParticleAtPos(T inkable, Vec3d pos, float scale) {
        InkColor inkColor = inkable.getInkColor();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(inkColor.getId());
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeFloat(scale);

        if (inkable.getWorld() instanceof ServerWorld world) {
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, new BlockPos(pos))) {
                ServerPlayNetworking.send(player, INK_SQUID_SOUL_PARTICLE_AT_POS, buf);
            }
        }
    }
}
