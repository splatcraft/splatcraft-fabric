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
    }

    public static <T extends Entity & Inkable> void sendSquidTravelEffects(T inkable, Vec3d pos) {
        InkColor inkColor = inkable.getInkColor();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(inkColor.getId());
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);

        if (inkable.getWorld() instanceof ServerWorld world) {
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, new BlockPos(pos))) {
                ServerPlayNetworking.send(player, PLAY_SQUID_TRAVEL_EFFECTS, buf);
            }
        }
    }
}
